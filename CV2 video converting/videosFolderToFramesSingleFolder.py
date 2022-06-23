#this script will output a folder containing all frames in a video
# given a folder of videos

#Jobs: 
#Set the video path and frames path
#Set the frame rate
#Change the video names to Orange1.mp4, Orange2.mp4
#Set name

name = 'Orange'

videos_folder_path = '/Users/liam/Documents/FYP Food Datasets/videos/Videos 13:01:2020/'+name+'/'
#forward slash at start and end, no need to worry about spaces
saved_frames_path = '/Users/liam/Documents/FYP Food Datasets/Frames9Labels3Scenes/'


import cv2
import os

def getFrame(sec, path):
    vidcap.set(cv2.CAP_PROP_POS_MSEC,sec*1000)
    hasFrames,image = vidcap.read()

    if hasFrames:

        #(h, w) = image.shape[:2]
        #print("Before cropped: "+str(w)+" x "+str(h))

        #crop image
        cropped = image[:, 184:1264]
        #(h, w) = cropped.shape[:2]
        #print("After cropped: "+str(w)+" x "+str(h))

        #rotate the cropped image
        rotated = cv2.rotate(cropped, cv2.ROTATE_90_CLOCKWISE)

        #resize the cropped rotated image
        r = 150.0 / rotated.shape[1]
        dim = (150, int(rotated.shape[0] * r))
        
        resized = cv2.resize(rotated, dim, interpolation = cv2.INTER_AREA)

        (h, w) = resized.shape[:2]
        #print(str(w)+" x "+str(h))

        cv2.imwrite(path+name+"--"+str(count)+".jpg", resized)     # save frame as JPG file
    return hasFrames













#make directory for frames
path = saved_frames_path+"/"+name+"/"
try:
    
    os.mkdir(path)
except OSError:
    print ("Creation of the directory %s failed" % path)
else:
    print ("Successfully created the directory %s " % path)



i = 0
num_videos = str(len(os.listdir(videos_folder_path)))
for video in os.listdir(videos_folder_path):
    i = i+1
    if video.endswith(".mp4"):
        vidcap = cv2.VideoCapture(videos_folder_path+video)#get video

        sec = 0
        frameRate = (1/30) #//it will capture image in each 0.5 seconds. (1/30) for all frames at 30fps
        count=1
        success = getFrame(sec, path)
        string_i = str(i)
        while success:
            print(string_i+"/"+num_videos)
            count = count + 1
            print("Frame: "+str(count))
            sec = sec + frameRate
            sec = round(sec, 2)
            success = getFrame(sec, path)
            #if count == 2:
            #    break









