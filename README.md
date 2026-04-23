# Wellness Companion (iOS & Android)

A premium, private, and offline-first "Wellness and Mental Health Sanctuary," engineered to match the standards of high-end wellness applications like Calm and Timely. Now featuring **Deep Synthesis** intelligence.

## Screenshots (Adversarial Overhaul)

<p align="center">
  <img src="../docs/screenshots/biometric_gate.svg" width="220" alt="Sanctuary Gate" />
  &nbsp;&nbsp;
  <img src="../docs/screenshots/mood_screen.svg" width="220" alt="The Check-in" />
</p>
<p align="center">
  <img src="../docs/screenshots/journal_screen.svg" width="220" alt="Synthesis Library" />
  &nbsp;&nbsp;
  <img src="../docs/screenshots/insights_screen.svg" width="220" alt="Pulse Patterns" />
</p>
<p align="center">
  <sub>
    Sanctuary Gate &nbsp;·&nbsp; Adversarial Check-in &nbsp;·&nbsp; Synthesis Library &nbsp;·&nbsp; Pulse Patterns
  </sub>
</p>

## Features

- **Deep Synthesis Engine** — automatically analyzes patterns across your last 3 reflections to find "Hidden Threads" and "Unspoken Tensions."
- **The Mirror (Adversarial AI)** — a sharp, intellectually challenging reflection guide that removes "therapeutic filler" to help you face the hard truth.
- **Mirror Avatar** — a high-fidelity animated AI persona (video-based) that provides a living presence during your sanctuary sessions.
- **Liquid Breathing Background** — a cinema-grade background with a synchronized 8-second breathing rhythm (expansion/opacity) for deep immersion.
- **Adaptive Atmosphere** — a generative ambient soundscape that shifts timbre and frequency based on your current emotional valence.
- **Ink-in-Water Reveal** — sequential word animations and high-impact Zen quotes that adapt to your mood.

## Architecture

- **Fragment-Based UI**: Swaps views in-place for a cohesive, dialog-free organic flow across iOS and Android.
- **Offline Intelligence**: Private, on-device AI powered by `llama.cpp` (2026 API) with 64KB chunked buffering for rapid model synchronization.
- **Production Media**: Performance-optimized headers utilizing curated atmospheric imagery (Unsplash CC0).

## Building & Running (iOS)

1. **Install XcodeGen**
   ```bash
   brew install xcodegen
   ```

2. **Generate and Build**
   ```bash
   xcodegen generate
   xcodebuild build -project WellnessCompanion.xcodeproj -scheme WellnessCompanion -destination 'platform=iOS Simulator,name=iPhone 17'
   ```

## Building & Running (Android)

```bash
./gradlew assembleDebug
```

---
<sub>*Photography by Unsplash (Public Domain / CC0). Animated loops by Mixkit.*</sub>
