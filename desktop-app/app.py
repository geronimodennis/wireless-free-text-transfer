"""Offline desktop QR text transfer utility.

Features:
- Encode text to QR code.
- Decode QR code from image file.
- Decode QR code from webcam.
"""

from __future__ import annotations

import threading
from pathlib import Path
import tkinter as tk
from tkinter import filedialog, messagebox

import cv2
import qrcode
from PIL import Image, ImageTk


RESAMPLE = Image.Resampling.LANCZOS if hasattr(Image, "Resampling") else Image.LANCZOS


class DesktopQrTransferApp:
    def __init__(self, root: tk.Tk) -> None:
        self.root = root
        self.root.title("Wireless Free Text Transfer - Desktop")
        self.root.geometry("1050x680")
        self.root.minsize(900, 580)

        self.detector = cv2.QRCodeDetector()
        self.scan_thread: threading.Thread | None = None
        self.scan_stop_event = threading.Event()
        self.generated_qr_image: Image.Image | None = None
        self.qr_photo: ImageTk.PhotoImage | None = None

        self.status_var = tk.StringVar(value="Ready.")

        self._build_ui()
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)

    def _build_ui(self) -> None:
        self.root.columnconfigure(0, weight=3)
        self.root.columnconfigure(1, weight=2)
        self.root.rowconfigure(0, weight=1)

        editor_frame = tk.Frame(self.root, padx=12, pady=12)
        editor_frame.grid(row=0, column=0, sticky="nsew")
        editor_frame.rowconfigure(1, weight=1)
        editor_frame.columnconfigure(0, weight=1)

        title = tk.Label(
            editor_frame,
            text="Plaintext",
            font=("Helvetica", 15, "bold"),
            anchor="w",
        )
        title.grid(row=0, column=0, sticky="ew", pady=(0, 8))

        self.text_area = tk.Text(editor_frame, wrap=tk.WORD, font=("Menlo", 12), undo=True)
        self.text_area.grid(row=1, column=0, sticky="nsew")

        scrollbar = tk.Scrollbar(editor_frame, command=self.text_area.yview)
        scrollbar.grid(row=1, column=1, sticky="ns")
        self.text_area.configure(yscrollcommand=scrollbar.set)

        action_frame = tk.Frame(editor_frame)
        action_frame.grid(row=2, column=0, sticky="ew", pady=(10, 0))

        buttons = [
            ("Generate QR", self.generate_qr),
            ("Decode QR From Image", self.decode_qr_from_image),
            ("Start Camera Scan", self.start_camera_scan),
            ("Stop Camera Scan", self.stop_camera_scan),
            ("Copy Text", self.copy_text),
            ("Clear Text", self.clear_text),
            ("Save QR PNG", self.save_qr_png),
        ]

        for index, (label, command) in enumerate(buttons):
            button = tk.Button(action_frame, text=label, command=command, padx=10, pady=8)
            button.grid(row=0, column=index, sticky="ew", padx=(0, 6))
            action_frame.columnconfigure(index, weight=1)

        preview_frame = tk.Frame(self.root, padx=12, pady=12)
        preview_frame.grid(row=0, column=1, sticky="nsew")
        preview_frame.rowconfigure(1, weight=1)
        preview_frame.columnconfigure(0, weight=1)

        preview_title = tk.Label(
            preview_frame,
            text="Generated QR",
            font=("Helvetica", 15, "bold"),
            anchor="w",
        )
        preview_title.grid(row=0, column=0, sticky="ew", pady=(0, 8))

        self.qr_label = tk.Label(
            preview_frame,
            text="Generate a QR code to preview it here.",
            justify=tk.CENTER,
            relief=tk.GROOVE,
            borderwidth=1,
        )
        self.qr_label.grid(row=1, column=0, sticky="nsew")

        status = tk.Label(
            self.root,
            textvariable=self.status_var,
            anchor="w",
            relief=tk.SUNKEN,
            padx=8,
        )
        status.grid(row=1, column=0, columnspan=2, sticky="ew")

    def get_text(self) -> str:
        return self.text_area.get("1.0", tk.END).rstrip("\n")

    def set_text(self, value: str) -> None:
        self.text_area.delete("1.0", tk.END)
        self.text_area.insert(tk.END, value)

    def generate_qr(self) -> None:
        text = self.get_text()
        if not text.strip():
            messagebox.showwarning("No text", "Please enter text before generating a QR code.")
            return

        qr = qrcode.QRCode(
            version=None,
            error_correction=qrcode.constants.ERROR_CORRECT_M,
            box_size=10,
            border=2,
        )
        qr.add_data(text)
        qr.make(fit=True)

        image = qr.make_image(fill_color="black", back_color="white").convert("RGB")
        self.generated_qr_image = image
        self._render_qr_preview(image)
        self.status_var.set("QR generated from textarea text.")

    def _render_qr_preview(self, image: Image.Image) -> None:
        preview = image.copy()
        preview.thumbnail((430, 430), RESAMPLE)
        self.qr_photo = ImageTk.PhotoImage(preview)
        self.qr_label.configure(image=self.qr_photo, text="")

    def decode_qr_from_image(self) -> None:
        path = filedialog.askopenfilename(
            title="Select image",
            filetypes=[
                ("Image Files", "*.png *.jpg *.jpeg *.bmp *.webp"),
                ("All Files", "*.*"),
            ],
        )
        if not path:
            return

        image = cv2.imread(path)
        if image is None:
            messagebox.showerror("Read error", "Could not open selected image.")
            return

        decoded = self._decode_frame(image)
        if decoded:
            self.set_text(decoded)
            self.status_var.set(f"Decoded QR from image: {Path(path).name}")
        else:
            messagebox.showinfo("No QR found", "No readable QR code found in the selected image.")
            self.status_var.set("No QR detected in selected image.")

    def _decode_frame(self, frame) -> str:
        data, _points, _ = self.detector.detectAndDecode(frame)
        if data:
            return data

        found, decoded_list, _points, _ = self.detector.detectAndDecodeMulti(frame)
        if found:
            for item in decoded_list:
                if item:
                    return item
        return ""

    def start_camera_scan(self) -> None:
        if self.scan_thread and self.scan_thread.is_alive():
            self.status_var.set("Camera scan is already running.")
            return

        self.scan_stop_event.clear()
        self.scan_thread = threading.Thread(target=self._camera_scan_loop, daemon=True)
        self.scan_thread.start()
        self.status_var.set("Camera scanner started. Point webcam at QR code.")

    def _camera_scan_loop(self) -> None:
        cap = cv2.VideoCapture(0)
        if not cap.isOpened():
            self.root.after(0, lambda: messagebox.showerror("Camera error", "Cannot open webcam."))
            self.root.after(0, lambda: self.status_var.set("Failed to start camera scan."))
            return

        window_name = "QR Scanner - press Q or Esc to cancel"

        try:
            while not self.scan_stop_event.is_set():
                ok, frame = cap.read()
                if not ok:
                    continue

                decoded = self._decode_frame(frame)
                if decoded:
                    self.root.after(0, lambda value=decoded: self.set_text(value))
                    self.root.after(0, lambda: self.status_var.set("QR detected from webcam."))
                    self.scan_stop_event.set()

                cv2.imshow(window_name, frame)
                key = cv2.waitKey(1) & 0xFF
                if key in (27, ord("q")):
                    self.scan_stop_event.set()

        finally:
            cap.release()
            cv2.destroyAllWindows()

    def stop_camera_scan(self) -> None:
        if self.scan_thread and self.scan_thread.is_alive():
            self.scan_stop_event.set()
            self.status_var.set("Camera scan stopping...")
        else:
            self.status_var.set("Camera scanner is not running.")

    def copy_text(self) -> None:
        text = self.get_text()
        if not text:
            self.status_var.set("Nothing to copy.")
            return

        self.root.clipboard_clear()
        self.root.clipboard_append(text)
        self.status_var.set("Textarea text copied to clipboard.")

    def clear_text(self) -> None:
        self.text_area.delete("1.0", tk.END)
        self.status_var.set("Textarea cleared.")

    def save_qr_png(self) -> None:
        if self.generated_qr_image is None:
            messagebox.showinfo("No QR yet", "Generate a QR code first.")
            return

        path = filedialog.asksaveasfilename(
            title="Save QR image",
            defaultextension=".png",
            filetypes=[("PNG", "*.png")],
            initialfile="text-transfer-qr.png",
        )
        if not path:
            return

        self.generated_qr_image.save(path, format="PNG")
        self.status_var.set(f"QR image saved: {path}")

    def on_close(self) -> None:
        self.scan_stop_event.set()
        cv2.destroyAllWindows()
        self.root.destroy()


def main() -> None:
    root = tk.Tk()
    app = DesktopQrTransferApp(root)
    root.mainloop()


if __name__ == "__main__":
    main()
