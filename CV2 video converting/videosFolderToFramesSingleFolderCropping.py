#Given a folder of videos, this script will output a folder containing all frames from every video

#The videos are cropped to 22 seconds so the frames from each video is the same. The original vid is preserved.
#The frames are cropped, rotated and resized

#Poblem: CrispsBlue video ending 403 runs forever


#Jobs: 
#Set the videos folder path and frames folder path
#Set the frame rate
#Set name

name = 'Orange'

videos_folder_path = '/Users/liam/Documents/FYP Food Datasets/Test Set 3 Scenes/Test Set 3 Scenes videos/'+name+'/'
#forward slash at start and end, no need to worry about spaces
saved_frames_path = '/Users/liam/Documents/FYP Food Datasets/Test Set 3 Scenes/Test Set 3 Scenes Frames/'




import cv2
import os
from moviepy.video.io.ffmpeg_tools import ffmpeg_extract_subclip

id = 1



def getFrame(sec, path, id):
    vidcap.set(cv2.CAP_PROP_POS_MSEC,sec*1000)
    hasFrames,image = vidcap.read()

    if hasFrames:

        (h, w) = image.shape[:2]
        #print("Before cropped: "+str(w)+" x "+str(h))





        if (w == 1920):#normal case
            #crop image
            cropped = image[:, 184:1264]

            #rotate the cropped image
            rotated = cv2.rotate(cropped, cv2.ROTATE_90_CLOCKWISE)

        elif (w==1080):#strange case
            #crop image
            cropped = image[184:1264, :]

            #no need to rotate
            rotated = cropped





        #resize the cropped rotated image
        r = 150.0 / rotated.shape[1]
        dim = (150, int(rotated.shape[0] * r))
        
        resized = cv2.resize(rotated, dim, interpolation = cv2.INTER_AREA)

        (h, w) = resized.shape[:2]
        #print(str(w)+" x "+str(h))

        cv2.imwrite(path+name+"--"+str(id)+".jpg", resized)     # save frame as JPG file
        print("Saved "+path+name+"--"+str(id)+".jpg")
    return hasFrames













#make directory for frames
path = saved_frames_path+name+"/"
try:
    
    os.mkdir(path)
except OSError:
    print ("Creation of the directory %s failed" % path)
else:
    print ("Successfully created the directory %s " % path)



i = 0
num_videos = str(len(os.listdir(videos_folder_path)))

a = os.listdir(videos_folder_path)

videos  = []

for v in a:
	if not (v.startswith(".") or v.startswith("(")):
		videos.append(v)


#for v in videos:
#    if (v[0:2] == "._"):
#        videos.remove(v)

num_videos = str(len(videos))

for video in videos:
    print("videos list: \n\n")
    print(videos)
    print("\n\n")
    i = i+1
    if video.endswith(".mp4"):

        #crop video
        print("Cropping "+str(video))
        ffmpeg_extract_subclip(videos_folder_path+video, 0, 22, targetname=videos_folder_path+"(cropped)"+video)
        print("Cropped "+str(video))

        vidcap = cv2.VideoCapture(videos_folder_path+"(cropped)"+video)#get video

        sec = 0
        frameRate = (1/30) #//it will capture image in each 0.5 seconds. (1/30) for all frames at 30fps
        count=1
        success = getFrame(sec, path, id)
        id = id + 1
        print("Frame: "+str(count))
        string_i = str(i)
        while success:
            print(string_i+"/"+num_videos)
            count = count + 1
            print("Frame: "+str(count))
            sec = sec + frameRate
            sec = round(sec, 2)
            success = getFrame(sec, path, id)
            id = id + 1
            #if count == 2:
            #    break

        os.remove(videos_folder_path+"(cropped)"+video)









