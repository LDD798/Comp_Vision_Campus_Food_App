from moviepy.video.io.ffmpeg_tools import ffmpeg_extract_subclip

ffmpeg_extract_subclip("testOrange.mp4", 0, 22, targetname="cropped.mp4")