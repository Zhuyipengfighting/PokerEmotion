import mne
import numpy as np
from sklearn.preprocessing import StandardScaler



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

# 通道映射
channel_names = ['Fp1', 'Fp2', 'F3', 'F4', 'F7', 'F8', 'T7', 'T8', 'C3', 'C4', 'P7', 'P8', 'P3', 'P4', 'O1', 'O2']
channel_mapping = {f'Channel_{i + 1}': channel_names[i] for i in range(16)}


# 选定的通道和采样率
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
step = 16  # 0.125秒（128Hz 下为 16 采样点）


# 计算微分熵
def differential_entropy_from_variance(var):
    return 0.5 * np.log(2 * np.pi * np.e * var)


def preprocess_data(data):
    eeg_data = data[[col for col in data.columns if 'Channel_' in col]].rename(columns=channel_mapping)

    # 创建 MNE RawArray
    info = mne.create_info(ch_names=list(channel_mapping.values()), sfreq=sfreq, ch_types='eeg')
    raw = mne.io.RawArray(eeg_data.T, info)

    # 预处理：滤波 & 陷波
    raw.filter(4, 45, fir_design='firwin')
    raw.notch_filter(50, filter_length='auto')  # 使用自动计算的较短滤波器长度
    # 重采样至 128Hz
    raw.resample(128)
    raw.pick_channels(selected_channels)
    raw.set_eeg_reference()

    # #可视化（要另下载matplotlib依赖包）
    # # 绘制 EEG 时域波形
    # raw.plot(scalings='auto', title="EEG 时域信号", duration=10, start=0)
    #
    # # 绘制功率谱密度 (PSD)
    # raw.plot_psd(fmin=4, fmax=45, average=True)
    # plt.show()

    # 返回预处理后的 EEG 数据
    return raw


def extract_features(data):
    sfreq = data.info['sfreq']
    data_arr = data.get_data()
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

    # 归一化处理
    scaler = StandardScaler()
    all_features_normalized = scaler.fit_transform(all_features)

    return all_features_normalized
