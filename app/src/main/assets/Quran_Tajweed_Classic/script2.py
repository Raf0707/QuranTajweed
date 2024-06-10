import cv2
import numpy as np

# Load the original image
image = cv2.imread('003.jpg')

# Define the green color range for the frame
green_lower = np.array([60, 100, 50])
green_upper = np.array([80, 255, 255])

# Convert the image to HSV color space
hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

# Create a mask using the green color range
mask = cv2.inRange(hsv, green_lower, green_upper)

# Find the contours of the mask
contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

# Find the largest contour (assuming the frame is the largest green area)
largest_contour = max(contours, key=cv2.contourArea)

# Get the bounding box of the largest contour
x, y, w, h = cv2.boundingRect(largest_contour)

# Crop the image using the bounding box
cropped_image = image[y:y+h, x:x+w]

# Define the white color for the top white rectangles
white = np.array([255, 255, 255])

# Convert the cropped image to grayscale
gray_image = cv2.cvtColor(cropped_image, cv2.COLOR_BGR2GRAY)

# Find the edges of the grayscale image
edges = cv2.Canny(gray_image, 50, 150)

# Find the contours of the edges
contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

# Find the top two contours (assuming the top two white rectangles are the largest)
top_contours = sorted(contours, key=cv2.contourArea, reverse=True)[:2]

# Find the top-most point of the top two contours
top_point = min((contour[0][0], contour[0][1]) for contour in top_contours)

# Crop the image from the top to the top-most point
cropped_image = cropped_image[top_point[1]:]

# Save the trimmed image
cv2.imwrite('003.jpg', cropped_image)
