import os
import numpy as np
from tensorflow.keras.models import load_model
from sklearn.preprocessing import StandardScaler
from collections import Counter  # 用于统计类别出现次数

new_data_path = r"E:\PycharmProjects\pythonProject3\original_data\feature_mydata"  # 新数据的路径

#定义情感标签映射
emotion_labels = {
    0: "沮丧、悲伤",
    1: "低自信、冷漠",
    2: "焦虑、紧张",
    3: "恼怒、恐惧",
    4: "满足、平静",
    5: "放松、自信",
    6: "激动、兴奋",
    7: "快乐、幸福"
}

#加载已训练的模型
model = load_model(r"E:\PycharmProjects\pythonProject3\original_data\my_model5.h5")

#初始化Scaler
scaler = StandardScaler()

#遍历文件夹中所有的.npy文件
for filename in os.listdir(new_data_path):
    if filename.endswith(".npy"):  # 仅处理.npy文件
        #加载新数据
        new_data = np.load(os.path.join(new_data_path, filename))

        #归一化处理
        new_data = scaler.fit_transform(new_data)

        # 计算特征维度
        num_channels = 10
        num_bands = 4
        feature_dim = num_channels * num_bands  # 10×4 = 40

        # 计算时间步数（有多少窗口）
        time_steps = new_data.shape[0]  # 每个窗口对应一个时间步
        print(f"File: {filename}, Time Steps: {time_steps}, Samples: {new_data.shape[0]}")

        #逐个时间步预测
        y_pred_classes = []

        for i in range(time_steps):
            single_step_data = new_data[i].reshape((1, 1, feature_dim))  # 变为 (1, 1, 40)
            y_pred = model.predict(single_step_data)  # 预测当前时间步
            y_pred_class = np.argmax(y_pred, axis=-1)[0]  # 获取类别
            y_pred_classes.append(y_pred_class)  # 存入预测类别列表

        #统计类别出现次数
        class_counts = Counter(y_pred_classes)  # 统计每个类别出现的次数
        total_predictions = time_steps  # 总时间步数

        #排序
        sorted_classes = class_counts.most_common()

        #主要情绪类别
        main_class, main_count = sorted_classes[0]
        main_emotion = emotion_labels[main_class]
        main_class_ratio = main_count / total_predictions

        #次要情绪类别
        if len(sorted_classes) > 1:
            secondary_class, secondary_count = sorted_classes[1]
            secondary_emotion = emotion_labels[secondary_class]
            secondary_class_ratio = secondary_count / total_predictions
        else:
            secondary_emotion = "无次要情感类别"
            secondary_class_ratio = 0

        #第三情绪类别
        if len(sorted_classes) > 2:
            tertiary_class, tertiary_count = sorted_classes[2]
            tertiary_emotion = emotion_labels[tertiary_class]
            tertiary_class_ratio = tertiary_count / total_predictions
        else:
            tertiary_emotion = "无第三情感类别"
            tertiary_class_ratio = 0

        print(f"预测 {filename} 主要情绪: {main_emotion} ({main_class_ratio:.4f})")
        print(f"预测 {filename} 次要情绪: {secondary_emotion} ({secondary_class_ratio:.4f})")
        print(f"预测 {filename} 第三情绪: {tertiary_emotion} ({tertiary_class_ratio:.4f})")



