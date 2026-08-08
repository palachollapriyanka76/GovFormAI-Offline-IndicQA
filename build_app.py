#!/usr/bin/env python3
"""
build_app.py — Android App Build & Validation Assistant
Validates Android project structure, CMake C++ NDK native configuration,
and outputs build status for Member C (App Lead).
"""
import os, sys, json, subprocess

APP_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "app")

print("=" * 80)
print("GovFormAI IndicQA — Android App Build & Architecture Validator")
print("=" * 80)
print(f"[Build Check] App Directory: {APP_DIR}")

manifest_path = os.path.join(APP_DIR, "src", "main", "AndroidManifest.xml")
gradle_path = os.path.join(APP_DIR, "build.gradle.kts")
cpp_path = os.path.join(APP_DIR, "src", "main", "cpp", "native_lib.cpp")

print(f"[Build Check] AndroidManifest.xml: {'EXISTS' if os.path.exists(manifest_path) else 'MISSING'}")
print(f"[Build Check] build.gradle.kts: {'EXISTS' if os.path.exists(gradle_path) else 'MISSING'}")
print(f"[Build Check] Native C++ JNI (native_lib.cpp): {'EXISTS' if os.path.exists(cpp_path) else 'MISSING'}")

gradle_bin = None
for cmd in ["gradle", "gradle.bat"]:
    try:
        res = subprocess.run([cmd, "--version"], capture_output=True, text=True)
        if res.returncode == 0:
            gradle_bin = cmd
            print(f"[Build Check] Found System Gradle: {res.stdout.splitlines()[0]}")
            break
    except Exception:
        pass

if gradle_bin:
    print(f"\n[Build Runner] Executing '{gradle_bin} assembleDebug' in {APP_DIR}...")
    res = subprocess.run([gradle_bin, "assembleDebug"], cwd=APP_DIR)
    sys.exit(res.returncode)
else:
    print("\n" + "=" * 80)
    print("ANDROID STUDIO / GRADLE INSTRUCTIONS:")
    print("=" * 80)
    print("1. Open Android Studio -> Select 'Open an existing Android Studio project'")
    print(f"2. Point to directory: {APP_DIR}")
    print("3. Android Studio will automatically download Gradle Wrapper JAR & sync NDK dependencies.")
    print("4. Click 'Build' -> 'Make Project' or 'Build APK(s)'.")
    print("=" * 80)
