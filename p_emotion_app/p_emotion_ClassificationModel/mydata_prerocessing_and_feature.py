import os
import numpy as np
import pandas as pd
import mne
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler
import joblib

# 设置原始数据目录
data_dir = r"E:\PycharmProjects\pythonProject3\original_data\original_mydata"
preprocessed_dir = r"E:\PycharmProjects\pythonProject3\original_data\preprocessing_mydata"
feature_dir = r"E:\PycharmProjects\pythonProject3\original_data\feature_mydata"

# 确保目标目录存在
os.makedirs(preprocessed_dir, exist_ok=True)
os.makedirs(feature_dir, exist_ok=True)



# # EEG通道映射
# channel_mapping = {
#     'EXG Channel 0': 'Fp1', 'EXG Channel 1': 'Fp2',
#     'EXG Channel 2': 'F3',  'EXG Channel 3': 'F4',
#     'EXG Channel 4': 'F7',  'EXG Channel 5': 'F8',
#     'EXG Channel 6': 'T7',  'EXG Channel 7': 'T8',
#     'EXG Channel 8': 'C3',  'EXG Channel 9': 'C4',
#     'EXG Channel 10': 'P7', 'EXG Channel 11': 'P8',
#     'EXG Channel 12': 'P3', 'EXG Channel 13': 'P4',
#     'EXG Channel 14': 'O1', 'EXG Channel 15': 'O2'
# }

# EEG通道名称
channel_names = ['Fp1', 'Fp2', 'F3', 'F4', 'F7', 'F8', 'T7', 'T8',
                 'C3', 'C4', 'P7', 'P8', 'P3', 'P4', 'O1', 'O2']

# 创建通道映射字典，将 Channel_1 映射为 Fp1，依此类推
channel_mapping = {f'Channel_{i+1}': channel_names[i] for i in range(16)}

# 选定的通道
selected_channels = ['Fp1', 'Fp2', 'F3', 'F4', 'P3', 'P4', 'O1', 'O2', 'F7', 'F8']
sfreq = 150  # 原始采样率

# 频段定义
freq_bands = {
    'theta': (4, 8),
    'alpha': (8, 12),
    'beta': (12, 30),
    'gamma': (30, 45)
}

# 滑动窗口参数
win_length = 256  # 2秒（128Hz 下为 256 采样点）
step = 16        # 0.125秒（128Hz 下为 16 采样点）


# 计算微分熵
def differential_entropy_from_variance(var):
    return 0.5 * np.log(2 * np.pi * np.e * var)


# 处理所有 CSV 文件
for filename in os.listdir(data_dir):
    if filename.endswith(".csv"):
        file_path = os.path.join(data_dir, filename)
        print(f"Processing file: {file_path}")

        # 读取数据
        data = pd.read_csv(file_path)
        eeg_data = data[[col for col in data.columns if 'Channel_' in col]].rename(columns=channel_mapping)

        # 创建 MNE RawArray
        info = mne.create_info(ch_names=list(channel_mapping.values()), sfreq=sfreq, ch_types='eeg')
        raw = mne.io.RawArray(eeg_data.T, info)

        # 绘制 EEG 时域波形
        raw.plot(scalings='auto', title="EEG 时域信号", duration=10, start=0)

        # 预处理：滤波 & 陷波
        raw.filter(4, 45, fir_design='firwin')
        raw.notch_filter(50)

        # 重采样至 128Hz
        raw.resample(128)
        raw.pick_channels(selected_channels)
        raw.set_eeg_reference()

        # 保存预处理数据
        preprocessed_path = os.path.join(preprocessed_dir, filename.replace(".csv", ".dat"))
        raw.get_data().tofile(preprocessed_path)
        print(f"Saved preprocessed data to: {preprocessed_path}")

        # 保存预处理后的 EEG 数据为 CSV
        preprocessed_csv_path = os.path.join(preprocessed_dir, filename.replace(".csv", "_preprocessed.csv"))
        df_preprocessed = pd.DataFrame(raw.get_data().T, columns=raw.ch_names)
        df_preprocessed.to_csv(preprocessed_csv_path, index=False)
        print(f"Saved preprocessed CSV data to: {preprocessed_csv_path}")

        # 可视化 EEG 数据
        print(f"Visualizing file: {preprocessed_path}")
        data = np.fromfile(preprocessed_path, dtype=np.float64).reshape(len(selected_channels), -1)
        info = mne.create_info(ch_names=selected_channels, sfreq=128, ch_types='eeg')
        raw = mne.io.RawArray(data, info)

        # 绘制 EEG 时域波形
        raw.plot(scalings='auto', title="EEG 时域信号", duration=10, start=0)

        # 绘制功率谱密度 (PSD)
        raw.plot_psd(fmin=4, fmax=45, average=True)


        plt.show()

        # 计算微分熵特征
        sfreq = raw.info['sfreq']
        data_arr = raw.get_data()
        n_channels, n_times = data_arr.shape
        n_windows = (n_times - win_length) // step + 1

        features_by_band = {}
        for band, (l_freq, h_freq) in freq_bands.items():
            band_data = mne.filter.filter_data(data_arr, sfreq, l_freq=l_freq, h_freq=h_freq, verbose=False)
            band_features = np.zeros((n_windows, n_channels))
            for i in range(n_windows):
                start = i * step
                end = start + win_length
                window_data = band_data[:, start:end]
                var = np.var(window_data, axis=1)
                de_values = differential_entropy_from_variance(var)
                band_features[i, :] = de_values
            features_by_band[band] = band_features

        # 合并特征
        all_features = np.concatenate([features_by_band[band] for band in ['theta', 'alpha', 'beta', 'gamma']], axis=1)
        print("Extracted feature shape:", all_features.shape)


        # 保存特征数据
        feature_path = os.path.join(feature_dir, filename.replace(".csv", ".npy"))
        np.save(feature_path, all_features)
        print(f"Saved feature data to: {feature_path}")

        # 打印预处理后的 EEG 数据的前两行
        print("First two rows of preprocessed EEG data:")
        print(raw.get_data()[:, :2])

        # 打印特征数据的前两行
        print("First two rows of extracted features:")
        print(all_features[:2, :])

        # 打印提取特征的前30个窗口数据
        print("First 30 windows (0.125s each) of extracted features:")
        print(all_features[:30, :])

        # 将特征数据保存为 CSV 文件
        # 这里为 CSV 文件设置列名，比如 feature_1, feature_2, ..., feature_N
        n_features = all_features.shape[1]
        columns = [f"feature_{i + 1}" for i in range(n_features)]
        features_df = pd.DataFrame(all_features, columns=columns)

        # 构建 CSV 文件保存路径，这里在原有文件名基础上添加
        csv_feature_path = os.path.join(feature_dir, filename.replace(".csv", "_feature.csv"))
        features_df.to_csv(csv_feature_path, index=False)
        print(f"Saved feature data to: {csv_feature_path}")

print("All files processed successfully!")









# import numpy as np
# import pandas as pd
# import os
# import mne
# from scipy.signal import welch
#
# # EEG 预设参数
# SFREQ = 150  # 原始采样率
# SFREQ_RESAMPLED = 128  # 目标采样率
# FREQ_BANDS = {
#     'theta': (4, 8),
#     'alpha': (8, 12),
#     'beta': (12, 30),
#     'gamma': (30, 45)
# }
# WIN_LENGTH = int(0.5 * SFREQ_RESAMPLED)  # 0.5秒
# STEP = int(0.125 * SFREQ_RESAMPLED)      # 0.125秒
# SELECTED_CHANNELS = ['Fp1', 'Fp2', 'F3', 'F4', 'P3', 'P4', 'O1', 'O2', 'F7', 'F8']  # 选定通道
#
# # 计算特定频段功率
# def calculate_band_power(data, sfreq, fmin, fmax, nperseg, noverlap):
#     """ 计算给定频段的功率 """
#     freqs, psd = welch(data, fs=sfreq, nperseg=nperseg, noverlap=noverlap, nfft=2048)
#     mask = (freqs >= fmin) & (freqs <= fmax)
#     band_power = np.mean(psd[:, mask], axis=1)  # 计算目标频段的平均功率
#     return band_power
#
# # 计算滑动窗口的微分熵
# def calculate_differential_entropy(data, win_length, step):
#     """ 计算滑动窗口的微分熵 """
#     num_channels, num_samples = data.shape
#     num_windows = (num_samples - win_length) // step + 1
#     de_features = np.zeros((num_channels, num_windows))
#
#     for i in range(num_windows):
#         start = i * step
#         end = start + win_length
#         windowed_data = data[:, start:end]
#         variance = np.var(windowed_data, axis=1)  # 计算窗口内的方差
#         de_features[:, i] = 0.5 * np.log(2 * np.pi * np.e * variance)  # 计算微分熵
#
#     return de_features
#
# # 处理 EEG 数据
# def process_eeg_data(raw):
#     """ 预处理 EEG 并计算频段微分熵 """
#     raw.filter(4, 45, fir_design='firwin', l_trans_bandwidth=2, h_trans_bandwidth=11.25)
#     raw.notch_filter(50, filter_length='auto')
#     raw.resample(SFREQ_RESAMPLED)
#     raw.pick(SELECTED_CHANNELS)
#
#     montage = mne.channels.make_standard_montage('standard_1020')
#     raw.set_montage(montage)
#     raw.set_eeg_reference(ref_channels='average', projection=False)
#
#     # 计算每个频段的功率
#     data = raw.get_data()
#     band_de_features = {}
#
#     for band_name, (fmin, fmax) in FREQ_BANDS.items():
#         band_power = calculate_band_power(data, SFREQ_RESAMPLED, fmin, fmax, WIN_LENGTH, WIN_LENGTH - STEP)
#         band_de_features[band_name] = calculate_differential_entropy(band_power, WIN_LENGTH, STEP)
#
#     return band_de_features
#
# # 读取并处理 EEG 数据
# DATA_DIR = r"E:\PycharmProjects\pythonProject3\original_data\original_mydata"
# for filename in os.listdir(DATA_DIR):
#     if not filename.endswith(".csv"):
#         continue
#     file_path = os.path.join(DATA_DIR, filename)
#     print(f"Processing: {file_path}")
#
#     try:
#         # 读取 EEG 数据
#         data = pd.read_csv(file_path)
#         expected_columns = [f'Channel_{i+1}' for i in range(16)]
#         eeg_data = data[expected_columns].rename(columns={f'Channel_{i+1}': ch for i, ch in enumerate(SELECTED_CHANNELS)})
#
#         # 创建 MNE RawArray
#         info = mne.create_info(ch_names=SELECTED_CHANNELS, sfreq=SFREQ, ch_types='eeg')
#         raw = mne.io.RawArray(eeg_data.T, info)
#
#         # 计算特定频段微分熵
#         band_de_features = process_eeg_data(raw)
#
#         # 保存特征数据
#         feature_path = os.path.join(DATA_DIR, filename.replace(".csv", "_features.csv"))
#         df_features = pd.DataFrame()
#         for band, de_feature in band_de_features.items():
#             for i, channel in enumerate(SELECTED_CHANNELS):
#                 df_features[f"{channel}_{band}_DE"] = de_feature[i]
#
#         df_features.to_csv(feature_path, index=False)
#         print(f"Saved features: {feature_path}")
#
#     except Exception as e:
#         print(f"Error processing {filename}: {e}")



#     import numpy as np
#     import matplotlib.pyplot as plt
#     import mne
#
#     # 假设 raw 已经是处理过的 MNE RawArray 对象
#
#     # 计算频谱（功率谱密度）
#     spectra = raw.compute_psd(fmin=0, fmax=50, n_fft=2048, average='mean')  # 返回一个 Spectra 对象
#     psd = spectra.data  # 获取功率谱密度数据
#     freqs = spectra.freqs  # 获取频率数组
#
#     # 绘制 PSD 图
#     plt.figure(figsize=(10, 6))
#     for ch_idx, ch_name in enumerate(raw.info['ch_names']):
#         plt.semilogy(freqs, psd[ch_idx], label=ch_name)  # 使用对数尺度绘制
#     plt.title('Power Spectral Density (PSD)')
#     plt.xlabel('Frequency (Hz)')
#     plt.ylabel('Power Spectral Density (μV^2/Hz)')
#     plt.xlim(0, 50)  # 关注0-50Hz范围
#     plt.legend(loc='upper right')
#     plt.grid(True)
#     plt.show()
#
#     # 验证滤波效果：确保 4-45Hz 之间的功率较高，其他频率衰减
#     freq_band_4_45 = (freqs >= 4) & (freqs <= 45)
#     freq_band_outside = (freqs < 4) | (freqs > 45)
#
#     psd_4_45 = psd[:, freq_band_4_45].mean(axis=1)  # 在 4-45Hz 频段的平均功率
#     psd_outside = psd[:, freq_band_outside].mean(axis=1)  # 在频带外的平均功率
#
#     # 打印结果：比较 4-45Hz 与其他频段的功率
#     print("Mean power in 4-45Hz range (per channel):", psd_4_45)
#     print("Mean power outside 4-45Hz range (per channel):", psd_outside)
#
#     # 计算微分熵特征
#     sfreq = raw.info['sfreq']
#     data_arr = raw.get_data()
#     n_channels, n_times = data_arr.shape
#     n_windows = (n_times - WIN_LENGTH) // STEP + 1
#
#     features = []
#     for band, (l_freq, h_freq) in FREQ_BANDS.items():
#         band_data = mne.filter.filter_data(data_arr, sfreq, l_freq=l_freq, h_freq=h_freq, verbose=False)
#         band_features = np.zeros((n_windows, n_channels))
#         for i in range(n_windows):
#             window_data = band_data[:, i * STEP:i * STEP + WIN_LENGTH]
#             band_features[i, :] = differential_entropy(np.var(window_data, axis=1))
#         features.append(band_features)
#
#     all_features = np.concatenate(features, axis=1)
#     print("Extracted feature shape:", all_features.shape)
#
#     from sklearn.preprocessing import RobustScaler, MinMaxScaler
#
#     # 第一步：使用 RobustScaler 处理异常值
#     robust_scaler = RobustScaler()
#     X_robust = robust_scaler.fit_transform(all_features)
#
#     # 第二步：使用 MinMaxScaler 归一化到 [0,1]
#     minmax_scaler = MinMaxScaler()
#     all_features = minmax_scaler.fit_transform(X_robust)
#
#     # 打印归一化后的数据范围
#     print("Min:", all_features.min(), "Max:", all_features.max())
#
#     # 保存特征数据
#     feature_path = os.path.join(FEATURE_DIR, filename.replace(".csv", ".npy"))
#     np.save(feature_path, all_features)
#     print(f"Saved feature data: {feature_path}")
#
#     # 打印关键数据
#     print("First two rows of preprocessed EEG data:")
#     print(raw.get_data()[:, :2])
#
#     print("First two rows of extracted features:")
#     print(all_features[:2, :])
#
#     # 可视化前两个通道的微分熵特征
#     import matplotlib.pyplot as plt
#
#     plt.figure(figsize=(12, 6))
#     plt.plot(all_features[:30, :])  # 前30个窗口的特征
#     plt.title("Differential Entropy Features (First 30 windows)")
#     plt.xlabel("Features")
#     plt.ylabel("Feature Value")
#     plt.show()
#
#     import seaborn as sns
#
#     # 可视化前30个窗口的微分熵特征热图
#     sns.heatmap(all_features[:30, :], cmap="viridis", xticklabels=feature_columns)
#     plt.title("Heatmap of Differential Entropy Features (First 30 windows)")
#     plt.xlabel("Features")
#     plt.ylabel("Time Windows")
#     plt.show()
#
#
# # **EEG 3D 频谱绘制**
#     frequency_bands = [(4, 8), (8, 12), (12, 30), (30, 45)]
#     channels_to_plot = [ch for ch in SELECTED_CHANNELS if ch in raw.ch_names]  # 只选 raw 中存在的通道
#     n_channels = len(channels_to_plot)
#
#     data, times = raw.get_data(picks=channels_to_plot), raw.times
#     # 计算每个频段的功率谱
#     power = []
#     for (fmin, fmax) in frequency_bands:
#         raw_filtered = raw.copy().filter(fmin, fmax, picks=channels_to_plot)
#         filtered_data = raw_filtered.get_data(picks=channels_to_plot)
#         power_band = np.mean(np.power(filtered_data, 2), axis=-1, keepdims=True)  # 形状 (n_channels, 1)
#         power.append(power_band)
#
#     power = np.array(power)  # 形状 (n_bands, n_channels, n_times)
#
#     # 创建 3D 画布
#     fig = plt.figure(figsize=(12, 8))
#     ax = fig.add_subplot(111, projection='3d')
#
#     # 创建网格
#     time_idx = np.arange(power.shape[2])  # 时间轴
#     channel_idx = np.arange(power.shape[1])  # 通道轴
#     frequency_idx = np.arange(power.shape[0])  # 频段轴
#
#     X, Y, Z = np.meshgrid(time_idx, channel_idx, frequency_idx, indexing="ij")
#
#     # 将数据展开为 1D
#     X = X.flatten()
#     Y = Y.flatten()
#     Z = Z.flatten()
#     power_values = power[Z, Y, X]  # 取出对应的功率值
#
#     # 绘制 3D 功率光谱
#     ax.scatter(X, Y, Z, c=power_values, cmap='viridis')
#
#     ax.set_xlabel('Time (samples)')
#     ax.set_ylabel('Channels')
#     ax.set_zlabel('Frequency Bands')
#     ax.set_title('EEG Power Spectrogram (Time x Channels x Frequency Bands)')
#
#     plt.show()
#
#
# # **1. 实时EEG波形**
#     plt.figure(figsize=(12, 6))
#     plt.plot(raw.times[:300], raw.get_data()[:, :300].T)
#     plt.title("Real-time EEG Signals")
#     plt.xlabel("Time (s)")
#     plt.ylabel("Amplitude (μV)")
#     plt.show()
#
#     # **2. 频谱分析（柱状图 + 热力图）**
#     plt.figure(figsize=(10, 6))
#     plt.bar(FREQ_BANDS.keys(), np.mean(all_features, axis=0)[:len(FREQ_BANDS)])
#     plt.title("Frequency Band Power")
#     plt.show()
#
#     sns.heatmap(all_features[:30, :], cmap="viridis", xticklabels=[f"{ch}_{band}" for band in FREQ_BANDS.keys() for ch in SELECTED_CHANNELS])
#     plt.title("Heatmap of Differential Entropy Features")
#     plt.xlabel("Features")
#     plt.ylabel("Time Windows")
#     plt.show()
#
#     # **3. 头皮热力图 (Topomap)**
#     mne.viz.plot_topomap(np.mean(all_features, axis=0)[:len(SELECTED_CHANNELS)], raw.info, show=True)
#
#     # **4. 情绪趋势（折线图）**
#     plt.figure(figsize=(10, 5))
#     plt.plot(all_features[:, 0], label='Theta Power')
#     plt.plot(all_features[:, 1], label='Alpha Power')
#     plt.plot(all_features[:, 2], label='Beta Power')
#     plt.plot(all_features[:, 3], label='Alpha Power')
#     plt.title("EEG Feature Trends")
#     plt.xlabel("Time Windows")
#     plt.ylabel("Feature Value")
#     plt.legend()
#     plt.show()
#
#
# print("All files processed successfully!")







