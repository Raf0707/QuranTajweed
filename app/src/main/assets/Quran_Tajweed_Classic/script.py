import cv2
import numpy as np
import os

def trim_image(image_path):
    image = cv2.imread(image_path)
    if image is None:
        print(f"Не удалось загрузить изображение из {image_path}")
        return

    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    green_lower = np.array([60, 100, 50])
    green_upper = np.array([80, 255, 255])
    mask = cv2.inRange(hsv, green_lower, green_upper)
    mask_not = cv2.bitwise_not(mask)
    cropped_image = cv2.bitwise_and(image, image, mask=mask_not)

    # Найдем границы изображения после обработки маски
    gray = cv2.cvtColor(cropped_image, cv2.COLOR_BGR2GRAY)
    _, thresh = cv2.threshold(gray, 1, 255, cv2.THRESH_BINARY)
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    if contours:
        largest_contour = max(contours, key=cv2.contourArea)
        x, y, w, h = cv2.boundingRect(largest_contour)
        trimmed_image = image[y:y + h, x:x + w]
        
        # Определение пути для сохранения обрезанного изображения
        output_dir = r"C:\Users\09702\OneDrive\Изображения\qtt"
        if not os.path.exists(output_dir):
            os.makedirs(output_dir)
        filename = os.path.basename(image_path)
        output_path = os.path.join(output_dir, filename)
        cv2.imwrite(output_path, trimmed_image)
        print(f"Обрезанное изображение сохранено: {output_path}")

def process_images(directory):
    for filename in os.listdir(directory):
        if filename.endswith('.jpg') or filename.endswith('.png'):
            image_path = os.path.join(directory, filename)
            trim_image(image_path)

process_images(r"C:\Users\09702\OneDrive\Изображения\qtt")
