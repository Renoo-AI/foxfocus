// Audio Engine for FoxFocus Web & PC app

class AudioEngine {
  constructor() {
    this.soundMuted = false;
    this.bgMusicMuted = false;
    this.currentBgMusic = null;
    this.audioCache = {};
  }

  playSfx(sfxName) {
    if (this.soundMuted) return;
    try {
      const audioPath = `/medias/${sfxName}`;
      const audio = new Audio(audioPath);
      audio.volume = 0.7;
      audio.play().catch((err) => console.log('Audio autoplay prevented or error:', err));
    } catch (e) {
      console.warn('SFX play failed', e);
    }
  }

  playBgMusic(musicName = 'lo-fi-1.mp3') {
    if (this.bgMusicMuted) return;
    this.stopBgMusic();
    try {
      const audioPath = `/medias/${musicName}`;
      this.currentBgMusic = new Audio(audioPath);
      this.currentBgMusic.loop = true;
      this.currentBgMusic.volume = 0.35;
      this.currentBgMusic.play().catch((err) => console.log('Bg music autoplay error:', err));
    } catch (e) {
      console.warn('Bg music failed', e);
    }
  }

  stopBgMusic() {
    if (this.currentBgMusic) {
      this.currentBgMusic.pause();
      this.currentBgMusic = null;
    }
  }

  toggleSfx() {
    this.soundMuted = !this.soundMuted;
    return this.soundMuted;
  }

  toggleBgMusic(musicName = 'lo-fi-1.mp3') {
    this.bgMusicMuted = !this.bgMusicMuted;
    if (this.bgMusicMuted) {
      this.stopBgMusic();
    } else {
      this.playBgMusic(musicName);
    }
    return this.bgMusicMuted;
  }
}

export const audioEngine = new AudioEngine();
