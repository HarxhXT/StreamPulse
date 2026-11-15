# StreamPulse Product Specification

## Product
A professional Android gaming livestream studio for YouTube and Kick.

## Core user experience
- Sign in through official YouTube/Kick authorization flows where API access permits.
- Set stream title and destination before going live.
- Scenes: Starting Soon, Gameplay, BRB/Privacy, custom image/text, Ending.
- Floating in-app/overlay controls for chat, viewers and stream controls.
- Portrait and landscape output.
- Auto quality and manual presets (480p, 720p, 1080p), FPS and bitrate controls.
- Microphone and supported internal-audio controls.
- Switch to a privacy scene when the selected game is exited or capture is interrupted.

## Engineering constraints
- Android MediaProjection requires explicit user consent and an ongoing foreground service.
- Internal audio capture depends on Android version, device support, and app capture policy.
- Overlay exclusion from MediaProjection must be verified on real devices; do not assume every overlay
  is automatically excluded. Keep stream composition separate from control UI.
- App/game exit detection is imperfect. Provide a manual privacy control and fail-safe scene.
- YouTube API integration requires OAuth and supported Live Streaming API operations.
- Kick integration depends on the current official API, scopes, and app permissions.
- Simultaneous output may require a relay to avoid doubling the phone's upload bandwidth.

## Milestone 1 scope
- Kotlin + Jetpack Compose app shell.
- Runtime notification and MediaProjection permission flow.
- Foreground service lifecycle placeholder.
- GitHub Actions debug APK workflow.

Milestone 1 does not encode, publish, or start a real livestream.
