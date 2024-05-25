import pandas as pd
import matplotlib.pyplot as plt

csv_file = 'RSID_2_.csv'
data = pd.read_csv(csv_file)

if data.shape[1] < 5:
    raise ValueError("The CSV file should have at least 5 columns")

x = data.iloc[:, 0]
y1 = data.iloc[:, 1]
y2 = data.iloc[:, 2]
y3 = data.iloc[:, 3]
y4 = data.iloc[:, 4]

plt.figure(figsize=(10, 8))

plt.subplot(2, 2, 1)
plt.plot(x, y1, marker='o')
plt.title('Num Routes')
plt.xlabel('iteration')
plt.ylabel('num routes')

plt.subplot(2, 2, 2)
plt.plot(x, y2, marker='o')
plt.title('Service Cost')
plt.xlabel('x')
plt.ylabel('y2')

plt.subplot(2, 2, 3)
plt.plot(x, y3, marker='o')
plt.title('Distance')
plt.xlabel('x')
plt.ylabel('y3')

plt.subplot(2, 2, 4)
plt.plot(x, y4, marker='o')
plt.title('Fitness')
plt.xlabel('x')
plt.ylabel('y4')

plt.tight_layout()
plt.show()
