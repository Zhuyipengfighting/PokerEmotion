import mne
import numpy as np
import os
import pickle
import pandas as pd

# 禁用 MNE 的冗余日志
mne.set_log_level("WARNING")

def load_labels(csv_path):
    df = pd.read_csv(csv_path, sep=',', encoding='utf-8-sig')
    labels_dict = {}

    for _, row in df.iterrows():
        sub_id = f"{int(row['Participant_id']):02d}"
        trial_id = row["Experiment_id"] - 1
        valence = row["Valence"]
        arousal = row["Arousal"]
        dominance = row["Dominance"]
        liking = row["Liking"]

        if sub_id not in labels_dict:
            labels_dict[sub_id] = {}
        labels_dict[sub_id][trial_id] = [valence, arousal, dominance, liking]

    # 打印受试者 01 的标签
    print("受试者 01 的标签:")
    print(labels_dict.get("01", {}))

    return labels_dict


def preprocess_bdf_to_dat(bdf_path, output_dir, subject_id, labels_dict):
    raw = mne.io.read_raw_bdf(bdf_path, preload=True, verbose=False)
    eeg_channels = [
        'Fp1', 'AF3', 'F3', 'F7', 'FC5', 'FC1', 'C3', 'T7', 'CP5', 'CP1',
        'P3', 'P7', 'PO3', 'O1', 'Oz', 'Pz', 'Fp2', 'AF4', 'Fz', 'F4', 'F8',
        'FC6', 'FC2', 'Cz', 'C4', 'T8', 'CP6', 'CP2', 'P4', 'P8', 'PO4', 'O2'
    ]
    raw.pick_channels(eeg_channels)
    raw.resample(128, npad="auto")
    data, _ = raw.get_data(return_times=True)

    fs = 128  # 降采样后的采样率
    trial_duration = 63  # 每个 Trial 的总时长 (3秒基线 + 60秒实验)
    n_trials = 40
    trial_samples = trial_duration * fs  # 63s × 128Hz = 8064 点
    trials = []

    for i in range(n_trials):
        start = i * trial_samples
        end = start + trial_samples
        trial_data = data[:, start:end]

        # 基线校正
        baseline = trial_data[:, :3 * fs]  # 前3秒基线
        experiment = trial_data[:, 3 * fs:]  # 后60秒实验数据
        baseline_mean = np.mean(baseline, axis=1, keepdims=True)
        experiment_corrected = experiment - baseline_mean

        trials.append(experiment_corrected)

    #加载标签数据
    subject_labels = labels_dict.get(subject_id, {})
    labels = np.zeros((40, 4))  # 40 trials × 4 个标签

    for trial_id in range(40):
        if trial_id in subject_labels:
            labels[trial_id] = subject_labels[trial_id]
        else:
            print(f"警告: 受试者 {subject_id} 的试验 {trial_id} 标签缺失，使用默认值 5.0")
            labels[trial_id] = [5.0, 5.0, 5.0, 5.0]  # 中性值填充

    print(f"受试者 {subject_id} 标签: {labels}")

    #保存为 DAT 文件
    deap_data = {
        "data": np.array(trials),  # 形状 (40, 32, 7680)
        "labels": labels  # 形状 (40, 4)
    }

    output_path = os.path.join(output_dir, f"s{subject_id}.dat")
    with open(output_path, "wb") as f:
        pickle.dump(deap_data, f)
    print(f"已保存: {output_path}")


if __name__ == "__main__":
    # 输入参数配置
    input_dir = r"E:\PycharmProjects\pythonProject3\original_data\original_data"  # 原始 BDF 文件路径
    output_dir = r"E:\PycharmProjects\pythonProject3\original_data\preprocessing_data"  # 输出 DAT 文件路径
    labels_csv = r"E:\PycharmProjects\pythonProject3\original_data\participant_ratings.csv"  # 标签文件路径

    # 创建输出目录
    os.makedirs(output_dir, exist_ok=True)

    # 加载标签数据
    labels_dict = load_labels(labels_csv)

    # 遍历所有受试者 (01-32)
    for subject_id in [f"{i:02d}" for i in range(1, 33)]:
        bdf_path = os.path.join(input_dir, f"s{subject_id}.bdf")
        if os.path.exists(bdf_path):
            print(f"正在处理: s{subject_id}.bdf")
            preprocess_bdf_to_dat(
                bdf_path=bdf_path,
                output_dir=output_dir,
                subject_id=subject_id,
                labels_dict=labels_dict
            )
        else:
            print(f"错误: 文件 {bdf_path} 不存在！")
