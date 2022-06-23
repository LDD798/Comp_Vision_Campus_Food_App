import cv2

video_path = '/Users/liam/Downloads/Video/banana.mp4'
saved_frames_path = '/Users/liam/Downloads/TestFrames/'

vidcap = cv2.VideoCapture(video_path)
def getFrame(sec):
    vidcap.set(cv2.CAP_PROP_POS_MSEC,sec*1000)
    hasFrames,image = vidcap.read()

    if hasFrames:


        (h, w) = image.shape[:2]
        print("Before cropped: "+str(w)+" x "+str(h))

        if (w == 1920):#normal case
            #crop image
            cropped = image[:, 184:1264]
            (h, w) = cropped.shape[:2]
            print("After cropped: "+str(w)+" x "+str(h))

            #rotate the cropped image
            rotated = cv2.rotate(cropped, cv2.ROTATE_90_CLOCKWISE)

        elif (w==1080):#strange case
            #crop image
            cropped = image[184:1264, :]
            (h, w) = cropped.shape[:2]
            print("After cropped: "+str(w)+" x "+str(h))

            rotated = cropped
            



        #resize the cropped rotated image
        r = 150.0 / rotated.shape[1]
        dim = (150, int(rotated.shape[0] * r))
        
        resized = cv2.resize(rotated, dim, interpolation = cv2.INTER_AREA)

        (h, w) = resized.shape[:2]
        print(str(w)+" x "+str(h))

        cv2.imwrite(saved_frames_path+"image"+str(count)+".jpg", resized)     # save frame as JPG file
    return hasFrames
sec = 0
frameRate = 0.5#(1/30) #//it will capture image in each 0.5 seconds. (1/30) for all frames at 30fps
count=1
success = getFrame(sec)
while success:
    count = count + 1
    print(count)
    sec = sec + frameRate
    sec = round(sec, 2)
    success = getFrame(sec)
    if count == 2:
        break