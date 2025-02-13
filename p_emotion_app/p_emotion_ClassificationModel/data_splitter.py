import os
import numpy as np
from tensorflow.keras.utils import to_categorical
from sklearn.model_selection import train_test_split

# 设置路径
process_data_path = r"E:\PycharmProjects\pythonProject3\original_data\feature_data4"


# 读取所有受试者数据
def load_data(subject_list):
    X, y = [], []
    for subject in subject_list:
        feature_path = os.path.join(process_data_path, f'processed_s{subject}_features.npy')
        category_path = os.path.join(process_data_path, f'processed_s{subject}_labels.npy')

        if os.path.exists(feature_path) and os.path.exists(category_path):
            features = np.load(feature_path)  # (样本数, 10 * 4)
            labels = np.load(category_path)
            X.append(features)
            y.append(labels)

    X = np.vstack(X)  # 合并所有受试者数据
    y = np.concatenate(y)
    return X, y


# 载入数据
subject_list = [f"{i:02d}" for i in range(1, 33)]  # 01-32
X, y = load_data(subject_list)

# 计算正确的时间步数（每个通道 4 个频带）
num_channels = 10
num_bands = 4
feature_dim = num_channels * num_bands  # 10×4 = 40
time_steps = X.shape[1] // feature_dim  # 计算窗口数

# 调整形状为 (样本数, 时间步, 特征数)
X = X.reshape((X.shape[0], time_steps, feature_dim))

# One-hot 编码标签
y = np.array([label % 8 for label in y])  # 将标签映射到 0 到 7
y = to_categorical(y, num_classes=8)

# 划分训练集和测试集
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y.argmax(axis=1))

# 保存训练集和测试集
train_data_path = r"E:\PycharmProjects\pythonProject3\original_data\splitter_data4"

# 确保保存路径存在
if not os.path.exists(train_data_path):
    os.makedirs(train_data_path)

# 保存训练集和测试集数据
np.save(os.path.join(train_data_path, 'X_train.npy'), X_train)
np.save(os.path.join(train_data_path, 'X_test.npy'), X_test)
np.save(os.path.join(train_data_path, 'y_train.npy'), y_train)
np.save(os.path.join(train_data_path, 'y_test.npy'), y_test)

