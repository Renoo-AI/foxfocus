import { initializeApp } from "firebase/app";
import { 
  getAuth, 
  GoogleAuthProvider, 
  signInWithPopup, 
  signInWithEmailAndPassword, 
  createUserWithEmailAndPassword, 
  signInAnonymously, 
  signOut,
  onAuthStateChanged,
  sendPasswordResetEmail,
  updateProfile
} from "firebase/auth";
import { 
  getFirestore, 
  doc, 
  getDoc, 
  setDoc, 
  updateDoc, 
  collection, 
  query, 
  orderBy, 
  limit, 
  onSnapshot, 
  serverTimestamp 
} from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyCu9vyCAnQYINHI-1ysxKALy9GMd6bdyeY",
  authDomain: "getfoxfocus.firebaseapp.com",
  projectId: "getfoxfocus",
  storageBucket: "getfoxfocus.firebasestorage.app",
  messagingSenderId: "891857313091",
  appId: "1:891857313091:web:a7936e424335569770a3f6",
  measurementId: "G-R2RXTECN9K"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const db = getFirestore(app);
export const googleProvider = new GoogleAuthProvider();

/**
 * Creates or syncs user profile document in Firestore.
 * ALL USERS RECEIVE FREE PREMIUM (`isPremium: true`).
 */
export async function syncUserProfile(user, additionalData = {}) {
  if (!user) return null;
  const userRef = doc(db, "users", user.uid);
  const snap = await getDoc(userRef);

  const defaultProfile = {
    uid: user.uid,
    displayName: user.displayName || additionalData.displayName || "Fox Pioneer",
    email: user.email || (user.isAnonymous ? "Guest Mode" : ""),
    avatarId: additionalData.avatarId || "finn_crown",
    bio: additionalData.bio || "Crushing distraction & mastering daily focus 🦊🔥",
    provider: user.isAnonymous ? "GUEST" : (user.providerData[0]?.providerId === "google.com" ? "GOOGLE" : "EMAIL"),
    isPremium: true, // 🔥 Premium Plan is FREE for ALL users
    level: additionalData.level || 5,
    xp: additionalData.xp || 1420,
    streakDays: additionalData.streakDays || 12,
    coinBalance: additionalData.coinBalance || 850,
    diamondBalance: additionalData.diamondBalance || 15.0,
    targetApps: additionalData.targetApps || ["Instagram", "TikTok", "YouTube", "Games"],
    dailyGoalMinutes: additionalData.dailyGoalMinutes || 60,
    updatedAt: serverTimestamp()
  };

  if (!snap.exists()) {
    await setDoc(userRef, defaultProfile);
    // Also sync to global leaderboard
    await setDoc(doc(db, "leaderboard", user.uid), {
      uid: user.uid,
      displayName: defaultProfile.displayName,
      avatarId: defaultProfile.avatarId,
      level: defaultProfile.level,
      xp: defaultProfile.xp,
      streakDays: defaultProfile.streakDays,
      isPremium: true,
      updatedAt: serverTimestamp()
    });
    return defaultProfile;
  } else {
    const existing = snap.data();
    // Enforce isPremium = true always
    if (!existing.isPremium) {
      await updateDoc(userRef, { isPremium: true });
    }
    return { ...existing, isPremium: true };
  }
}

export {
  signInWithPopup,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signInAnonymously,
  signOut,
  onAuthStateChanged,
  sendPasswordResetEmail,
  updateProfile,
  doc,
  getDoc,
  setDoc,
  updateDoc,
  collection,
  query,
  orderBy,
  limit,
  onSnapshot,
  serverTimestamp
};
