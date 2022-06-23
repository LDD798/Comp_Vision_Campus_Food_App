# import the necessary packages
import cv2
import os

 
def count_frames(path, override=False):
	# grab a pointer to the video file and initialize the total
	# number of frames read
	video = cv2.VideoCapture(path)
	total = 0
 
	# if the override flag is passed in, revert to the manual
	# method of counting frames
	if override:
		total = count_frames_manual(video)
 
	# release the video file pointer
	video.release()
 
	# return the total number of frames in the video
	return total



path = "/Users/liam/Documents/videoTOframesConverter test files/Videos/1.mp4"


num_frames = count_frames(path)
print(num_frames)
