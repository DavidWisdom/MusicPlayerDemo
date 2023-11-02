import re
import json
import requests
import os
# import cv2
# from os.path import dirname, join
# def say_hello(source_path):
#    source = cv2.imread(join(dirname(__file__), "source.jpg"))
#    template = cv2.imread(join(dirname(__file__), "template.jpg"))
#    result = cv2.matchTemplate(source, template, cv2.TM_CCOEFF_NORMED)
#    min_val, max_val, min_loc, max_loc = cv2.minMaxLoc(result)
#    threshold = 0.8
#    if max_val > threshold:
#        return max_loc
#    else:
#        return (-1, -1)
#         print(min_loc)
#     else:
#         print("False")

def say_hello(url):
    session = requests.session()
    # url = 'https://www.bilibili.com/video/BV1W44y1H7JB'
    headers = {
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 '
                      'Safari/537.36 Edg/106.0.1370.37',
        "Referer": "https://www.bilibili.com",
    }
    resp = session.get(url, headers=headers)
    title = re.findall(r'<title data-vue-meta="true">(.*?)_', resp.text)[0]
    play_info = re.findall(r'<script>window.__playinfo__=(.*?)</script>', resp.text)[0]
    json_data = json.loads(play_info)
    audio_url = json_data['data']['dash']['audio'][0]['backupUrl'][0]
    return audio_url
    # audio_content = session.get(audio_url, headers=headers).content
#     title = "test"
#     audio_content = "test"
#     with open(os.path.join(context, title + '.mp3'), 'w') as f:
#         f.write(audio_content)
# #     print("hello")