import pandas as pd

# مسیر فایل‌های ورودی/خروجی
input_csv = 'batch_task.csv'
output_txt = 'workflow.txt'

df = pd.read_csv(input_csv, header=None)

with open(output_txt, 'w') as f:
    for idx, row in df.iterrows():
        task_id = idx
        length = int(row[8]) * 1000  # ستون CPU → طول task
        input_size = 10  # MB فرضی
        output_size = 10  # MB فرضی
        pes = 1
        cost = 0.1
        deadline = (row[6] - row[5])  # مدت اجرای واقعی (تخمینی)

        f.write(f"TASK, {task_id}, {length}, {input_size}, {output_size}, {pes}, {cost}, {deadline}\n")
