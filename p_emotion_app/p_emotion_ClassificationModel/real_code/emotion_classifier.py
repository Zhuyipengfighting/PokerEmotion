from tensorflow.keras.models import load_model
import numpy as np
from collections import Counter

# 定义情感标签映射
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


# 加载训练好的模型
model = load_model('my_model5.h5')

def classify_emotion(features):

    # 计算特征维度
    num_channels = 10
    num_bands = 4
    feature_dim = num_channels * num_bands  # 10×4 = 40

    # 计算时间步数（有多少窗口）
    time_steps = features.shape[0]  # 每个窗口对应一个时间步

    # 预测
    y_pred = model.predict(features.reshape((time_steps, 1, feature_dim)))  # 预测所有时间步
    y_pred_classes = np.argmax(y_pred, axis=-1)  # 获取每个时间步的预测类别

    # 统计类别出现次数
    class_counts = Counter(y_pred_classes)  # 统计每个类别出现的次数
    total_predictions = time_steps  # 总时间步数

    # 排序
    sorted_classes = class_counts.most_common()

    # 主要情绪类别
    main_class, main_count = sorted_classes[0]
    main_emotion = emotion_labels[main_class]
    main_class_ratio = main_count / total_predictions

    # 次要情绪类别
    if len(sorted_classes) > 1:
        secondary_class, secondary_count = sorted_classes[1]
        secondary_emotion = emotion_labels[secondary_class]
        secondary_class_ratio = secondary_count / total_predictions
    else:
        secondary_emotion = "无次要情感类别"
        secondary_class_ratio = 0

    # 返回主要情绪类别及其占比，次要情绪类别及其占比
    return {
        "main_emotion": main_emotion,
        "main_class_ratio": main_class_ratio,
        "secondary_emotion": secondary_emotion,
        "secondary_class_ratio": secondary_class_ratio
    }
