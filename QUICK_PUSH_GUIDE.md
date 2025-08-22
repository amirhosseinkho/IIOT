# راهنمای سریع Push به GitHub

## مرحله 1: آماده‌سازی Git
```bash
# در پوشه پروژه
git init
git add .
git commit -m "Initial commit: IIoT Task Scheduling System with Enhanced EPO-CEIS"
```

## مرحله 2: ایجاد Repository در GitHub
1. به [GitHub.com](https://github.com) بروید
2. روی "New repository" کلیک کنید
3. نام: `IIoT-Scheduler`
4. توضیحات: "Comprehensive IIoT task scheduling framework with Enhanced EPO-CEIS algorithm"
5. Public انتخاب کنید
6. **مهم**: README را initialize نکنید (ما قبلاً داریم)

## مرحله 3: Push کردن
```bash
git remote add origin https://github.com/YOUR_USERNAME/IIoT-Scheduler.git
git branch -M main
git push -u origin main
```

## فایل‌هایی که آپلود می‌شوند:
✅ **کدها**: `src/` (Java), `analyze_results.py` (Python)
✅ **مستندات**: `README.md`, `PROJECT_REPORT.md`, `LICENSE`
✅ **نمودارها**: `analysis_plots/` (8 نمودار با کیفیت بالا)
✅ **نتایج**: `quick_results/` (داده‌های CSV)
✅ **تنظیمات**: `.gitignore`, `requirements.txt`

## فایل‌هایی که آپلود نمی‌شوند:
❌ **کلاس‌های Java**: `*.class`
❌ **فایل‌های IDE**: `.idea/`, `.vscode/`
❌ **فایل‌های موقت**: `*.tmp`, `*.log`
❌ **فایل‌های باینری**: `*.jar`, `*.tar.gz`

## نکات مهم:
- فایل `.gitignore` همه چیزهای اضافی را فیلتر می‌کند
- نمودارها با کیفیت 300 DPI آپلود می‌شوند
- پروژه آماده برای ارائه حرفه‌ای است
