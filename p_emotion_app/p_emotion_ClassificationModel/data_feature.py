import numpy as np
import pickle
import os
import warnings
from scipy.signal import butter, filtfilt
from imblearn.over_sampling import SMOTE  # 导入SMOTE库

# 忽略警告
warnings.filterwarnings('ignore')

# 设置数据存放路径
data_path = os.getcwd()
process_data_path = os.path.join(data_path, 'process_data')

if not os.path.exists(process_data_path):
    os.makedirs(process_data_path)

# 受试者列表
subject_list = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10',
                '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
                '21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31', '32']

# 定义带通滤波器函数
def butter_bandpass(lowcut, highcut, fs, order=3):
    nyq = 0.5 * fs
    low = lowcut / nyq
    high = highcut / nyq
    b, a = butter(order, [low, high], btype='band')
    return b, a

# 计算差分熵
def calculate_de(signal, lowcut, highcut, fs):
    try:
        b, a = butter_bandpass(lowcut, highcut, fs)
        filtered = filtfilt(b, a, signal)
        var = np.var(filtered)
        return 0.5 * np.log(2 * np.pi * np.e * var)
    except Exception as e:
        print(f"Error calculating DE: {e}")
        return 0.0


# 二值化映射函数
def binarize_value(value):
    if value <= 4:  # 低
        return 0
    else:  # 高
        return 1


# 生成二值化后的组合标签
def generate_combined_label(valence, arousal, dominance):
    # 对每个维度进行二值化
    valence_bin = binarize_value(valence)
    arousal_bin = binarize_value(arousal)
    dominance_bin = binarize_value(dominance)

    # 组合三个二进制值成一个 3 位的二进制数
    # 例如: valence_bin = 1, arousal_bin = 0, dominance_bin = 1 => 101
    combined_label = valence_bin * 4 + arousal_bin * 2 + dominance_bin
    return combined_label

# 特征提取部分，添加标签生成逻辑
def DE_Processing(sub, channel, band, window_size, step_size, sample_rate, data_path, process_data_path):
    meta_features = []
    meta_labels = []  # 保存标签
    meta_vad = []  # 保存原始的 valence, arousal, dominance

    file_path = os.path.join(data_path, f's{sub}.dat')

    with open(file_path, 'rb') as file:
        subject = pickle.load(file, encoding='bytes')
        print(f"加载受试者 {sub} 数据成功!")

    band_ranges = [(band[i], band[i + 1]) for i in range(len(band) - 1)]

    for i in range(40):  # 遍历每个试验
        data = subject["data"][i]
        labels = subject["labels"][i]
        valence, arousal, dominance = labels[0], labels[1], labels[2]

        # 使用生成的二值化标签
        combined_label = generate_combined_label(valence, arousal, dominance)

        start = 0
        while start + window_size <= data.shape[1]:
            window_features = []
            for j in channel:  # 遍历每个通道
                X = data[j, start:start + window_size]

                # 提取特征
                for low, high in band_ranges:
                    de = calculate_de(X, low, high, sample_rate)
                    window_features.append(de)

            # 添加特征和标签
            if len(window_features) == len(channel) * len(band_ranges):
                meta_features.append(window_features)
                meta_labels.append(combined_label)  # 保存标签
                meta_vad.append([valence, arousal, dominance])

            start += step_size  # 移动窗口

    # 使用SMOTE对少数类进行过采样
    smote = SMOTE(random_state=42)
    meta_features_resampled, meta_labels_resampled = smote.fit_resample(np.array(meta_features), np.array(meta_labels))

    # 保存数据
    np.save(os.path.join(process_data_path, f'processed_s{sub}_features.npy'), meta_features_resampled)
    np.save(os.path.join(process_data_path, f'processed_s{sub}_labels.npy'), meta_labels_resampled)
    np.save(os.path.join(process_data_path, f'processed_s{sub}_vad.npy'), np.array(meta_vad))
    print(f"受试者 {sub} 特征提取并增强完成，文件已保存！")


# 主函数
if __name__ == "__main__":
    band = [4, 8, 12, 30, 45]  # 频段范围 [4-8 Hz, 8-12 Hz, 12-30 Hz, 30-45 Hz]
    target_path = r"E:\PycharmProjects\pythonProject3\original_data\feature_data4"

    if not os.path.exists(target_path):
        os.makedirs(target_path)

    # 更新滑动窗口设置
    window_size = 256  # 每个窗口 2 秒
    step_size = 16   # 每次移动 16 个采样点

    for subject in subject_list[25:]:
        DE_Processing(subject,
                      channel=[0, 16, 2, 19, 10, 28, 13, 31, 3, 20],  # 只保留这10个通道
                      band=band,
                      window_size=window_size,
                      step_size=step_size,
                      sample_rate=128,
                      data_path=r"E:\PycharmProjects\pythonProject3\original_data\preprocessing_data",
                      process_data_path=target_path)