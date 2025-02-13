import os
import numpy as np
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Conv1D, MaxPooling1D, Dense, Dropout, BatchNormalization, Input, LayerNormalization, MultiHeadAttention, Add, GlobalAveragePooling1D
from tensorflow.keras.optimizers import Adam
import matplotlib.pyplot as plt
import pickle
from sklearn.metrics import confusion_matrix, classification_report
import seaborn as sns
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau

#宋体
plt.rcParams['font.family'] = ['SimSun']
plt.rcParams['axes.unicode_minus'] = False


# 设置路径
train_data_path = r"E:\PycharmProjects\pythonProject3\original_data\splitter_data4"

# 载入训练集和测试集数据
X_train = np.load(os.path.join(train_data_path, 'X_train.npy'))
X_test = np.load(os.path.join(train_data_path, 'X_test.npy'))
y_train = np.load(os.path.join(train_data_path, 'y_train.npy'))
y_test = np.load(os.path.join(train_data_path, 'y_test.npy'))

# Transformer 编码器
def transformer_encoder(inputs, head_size, num_heads, ff_dim, dropout):
    """Transformer Encoder Block"""
    x = MultiHeadAttention(num_heads=num_heads, key_dim=head_size)(inputs, inputs)
    x = Dropout(dropout)(x)
    x = Add()([x, inputs])  # 残差连接
    x = LayerNormalization(epsilon=1e-6)(x)

    x_ffn = Dense(ff_dim, activation="relu")(x)
    x_ffn = Dense(inputs.shape[-1])(x_ffn)
    x_ffn = Dropout(dropout)(x_ffn)
    x = Add()([x, x_ffn])  # 残差连接
    x = LayerNormalization(epsilon=1e-6)(x)

    return x

# CNN+Transformer
inputs = Input(shape=(X_train.shape[1], X_train.shape[2]))

# CNN超参数
num_filters = 128  # 滤波器
kernel_size = 3    # 卷积核
x = Conv1D(num_filters, kernel_size=kernel_size, activation='relu', padding='same')(inputs)
x = BatchNormalization()(x)

# 仅在时间步 > 1 时执行池化
if X_train.shape[1] > 1:
    x = MaxPooling1D(pool_size=2)(x)

x = Conv1D(num_filters * 2, kernel_size=kernel_size, activation='relu', padding='same')(x)
x = BatchNormalization()(x)

# 再次检查时间步是否大于 1
if x.shape[1] > 1:
    x = MaxPooling1D(pool_size=2)(x)

# Transformer 层
num_heads = 8
head_size = 32
ff_dim = 192
dropout_rate = 0.1
x = transformer_encoder(x, head_size=head_size, num_heads=num_heads, ff_dim=ff_dim, dropout=dropout_rate)

# 分类层
x = GlobalAveragePooling1D()(x)
x = Dense(128, activation='relu')(x)
x = Dropout(0.5)(x)  # 增加Dropout，防止过拟合
outputs = Dense(8, activation='softmax')(x)

# 编译模型
learning_rate = 0.0001  # 设置稍低的学习率
model = Model(inputs, outputs)
model.compile(optimizer=Adam(learning_rate=learning_rate),
              loss='categorical_crossentropy',
              metrics=['accuracy'])

# 设置学习率调整策略
lr_scheduler = ReduceLROnPlateau(monitor='val_loss', patience=5, factor=0.5, min_lr=1e-6)

model.summary()

# 训练模型
early_stopping = EarlyStopping(monitor='val_loss', patience=10, restore_best_weights=True)
history = model.fit(X_train, y_train, epochs=50, batch_size=32, validation_data=(X_test, y_test),
                    callbacks=[early_stopping, lr_scheduler])


# 保存训练历史
with open('training_history5.pkl', 'wb') as f:
    pickle.dump(history.history, f)

# 保存训练好的模型
model.save('my_model5.h5')

# 在测试集上进行预测
y_pred = model.predict(X_test)
y_pred_classes = np.argmax(y_pred, axis=1)  # 获取预测类别
y_true_classes = np.argmax(y_test, axis=1)  # 获取真实类别

# 打印部分结果
print(f"Predictions (first 10): {y_pred_classes[:10]}")
print(f"True Labels (first 10): {y_true_classes[:10]}")

# 评估模型
loss, acc = model.evaluate(X_test, y_test)
print(f"最终测试集准确率: {acc:.4f}")

# 可视化训练过程
plt.figure(figsize=(12, 6))

plt.subplot(1, 2, 1)
plt.plot(history.history['loss'], label='训练损失')
plt.plot(history.history['val_loss'], label='验证损失')
plt.title('损失变化')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()

plt.subplot(1, 2, 2)
plt.plot(history.history['accuracy'], label='训练准确率')
plt.plot(history.history['val_accuracy'], label='验证准确率')
plt.title('准确率变化')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()

plt.tight_layout()
plt.show()

# 混淆矩阵
cm = confusion_matrix(y_true_classes, y_pred_classes)

# 绘制混淆矩阵
plt.figure(figsize=(8, 6))
sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=[f'Class {i}' for i in range(8)], yticklabels=[f'Class {i}' for i in range(8)])
plt.title('混淆矩阵')
plt.xlabel('预测标签')
plt.ylabel('真实标签')
plt.show()

# 分类报告
report = classification_report(y_true_classes, y_pred_classes, target_names=[f'Class {i}' for i in range(8)])
print("分类报告:\n", report)
