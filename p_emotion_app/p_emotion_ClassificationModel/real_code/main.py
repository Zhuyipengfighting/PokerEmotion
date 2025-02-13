import pandas as pd
from preprocess_and_feature_data import preprocess_data
from preprocess_and_feature_data import extract_features
from emotion_classifier import classify_emotion

def main():
    # 加载数据
    data_path = r"E:\PycharmProjects\pythonProject3\original_data\original_mydata\放松_20250101_215130.csv"  # 替换为你的数据文件路径
    data = pd.read_csv(data_path)

    # 预处理数据
    raw = preprocess_data(data)

    # 提取特征
    features = extract_features(raw)

    # 分类情感
    emotion_result = classify_emotion(features)

    # 输出结果
    print("主要情绪:", emotion_result["main_emotion"])
    print("主要情绪占比:", emotion_result["main_class_ratio"])
    print("次要情绪:", emotion_result["secondary_emotion"])
    print("次要情绪占比:", emotion_result["secondary_class_ratio"])

if __name__ == "__main__":
    main()