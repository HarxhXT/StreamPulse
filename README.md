# StreamPulse

Android gaming livestream studio for YouTube and Kick.

## Project status
This is the clean starting project for StreamPulse. It is **not yet a working livestreaming app**.
The first milestone establishes the Android app shell, permissions, foreground capture-service
lifecycle, and GitHub Actions build workflow. Screen encoding, scenes, platform authorization,
chat, and RTMPS publishing are later milestones.

## Build
Open the `android` folder in Android Studio, or run:

```bash
cd android
./gradlew assembleDebug
```

The GitHub Actions workflow builds a debug APK when code is pushed to the repository.

## Planned milestones
1. Android app foundation and capture permission flow.
2. Screen capture pipeline (MediaProjection + VirtualDisplay).
3. H.264/AAC encoding and scene compositor.
4. YouTube authorization and live-stream management.
5. Kick authorization/API integration, subject to available API permissions.
6. Chat, viewer count, floating controls, privacy/BRB scenes.
7. Auto/manual quality, orientation, audio controls, device testing.

Never enter platform passwords into StreamPulse. Use official OAuth flows.
