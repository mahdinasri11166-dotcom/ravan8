# درمانگرنو — نسخه ۵.۱۴

اپلیکیشن اندروید حرفه‌ای مدیریت نوبت، مراجع، جلسات، آزمون‌ها و امور مالی برای روانشناسان و مشاوران.

## سازنده
**مهدی طریری** — Madhitariri@gmail.com

## ساخت روی GitHub (توصیه‌شده)

1. یک مخزن **خالی** در GitHub بسازید (مثلاً `DarmanagarNo`).
2. محتویات این پوشه را **در ریشهٔ مخزن** قرار دهید تا `gradlew` و پوشه `app/` در root باشند:
   ```text
   /
   ├── app/
   ├── gradle/
   ├── gradlew
   ├── build.gradle.kts
   ├── settings.gradle.kts
   └── .github/workflows/build.yml
   ```
3. Push روی `main` یا `master`.
4. از تب **Actions** وضعیت ساخت را ببینید.
5. پس از موفقیت، Artifact به نام `darmanagarno-release-v5.14.0` را دانلود کنید (فایل APK).

می‌توانید از **Actions → Build Release APK → Run workflow** هم دستی اجرا کنید.

## ساخت محلی

```bash
chmod +x gradlew
./gradlew testDebugUnitTest
./gradlew assembleRelease
```

خروجی: `app/build/outputs/apk/release/app-release.apk`

## نیازمندی‌ها
JDK 17 · minSdk 26 · targetSdk 35 · Kotlin 2.0 · Compose · Gradle 8.9

## نسخه ۵.۱۴ — امکانات
- قفل خودکار، قالب SOAP، پیشنهاد زمان خالی
- روند نمرات آزمون، داشبورد سلامت کاری
- پشتیبان رمزدار AES-GCM
- کلید امضای Release داخل `app/darmanagarno-release.jks` (برای CI)
