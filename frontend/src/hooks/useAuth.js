import { useCallback, useEffect, useMemo, useState } from "react";
import { logout, signIn, signUp } from "../api/authApi";
import { createApiClient } from "../api/client";
import { getProfile, updateNickname, updatePassword } from "../api/userApi";
import { clearTokens, readTokens, saveTokens } from "../utils/tokenStorage";

export default function useAuth(notify) {
  const [tokens, setTokens] = useState(readTokens);
  const [profile, setProfile] = useState(null);
  const [authMode, setAuthMode] = useState("signin");
  const [authForm, setAuthForm] = useState({ username: "", password: "", nickname: "" });
  const [profileForm, setProfileForm] = useState({ nickname: "", nowPassword: "", newPassword: "" });
  const [authLoading, setAuthLoading] = useState(false);
  const isSignedIn = Boolean(tokens.accessToken);

  const setAuthTokens = useCallback((nextTokens) => {
    saveTokens(nextTokens);
    setTokens(nextTokens);
  }, []);

  const signOutLocal = useCallback(() => {
    clearTokens();
    setTokens({});
    setProfile(null);
  }, []);

  const api = useMemo(
    () => createApiClient({ tokens, onTokensChanged: setAuthTokens, onUnauthorized: signOutLocal }),
    [setAuthTokens, signOutLocal, tokens],
  );

  const loadProfile = useCallback(async () => {
    if (!isSignedIn) return;
    try {
      const data = await getProfile(api);
      setProfile(data);
      setProfileForm((prev) => ({ ...prev, nickname: data.nickname ?? "" }));
    } catch (error) {
      notify(error.message, "error");
    }
  }, [api, isSignedIn, notify]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  async function handleAuthSubmit(event) {
    event.preventDefault();
    setAuthLoading(true);
    try {
      if (authMode === "signup") {
        await signUp(authForm);
        notify("가입이 완료됐습니다. 바로 로그인합니다.");
      }
      const nextTokens = await signIn(authForm);
      setAuthTokens(nextTokens);
      setAuthForm({ username: "", password: "", nickname: "" });
      setAuthMode("signin");
    } catch (error) {
      notify(error.message, "error");
    } finally {
      setAuthLoading(false);
    }
  }

  async function handleLogout() {
    try {
      if (tokens.accessToken) await logout(api);
    } catch {
      // Local logout is still expected when a token is already invalid.
    } finally {
      signOutLocal();
      notify("로그아웃했습니다.");
    }
  }

  async function handleUpdateNickname(event) {
    event.preventDefault();
    try {
      await updateNickname(api, profileForm.nickname);
      notify("닉네임을 저장했습니다.");
      loadProfile();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function handleUpdatePassword(event) {
    event.preventDefault();
    try {
      await updatePassword(api, profileForm.nowPassword, profileForm.newPassword);
      setProfileForm((prev) => ({ ...prev, nowPassword: "", newPassword: "" }));
      notify("비밀번호를 변경했습니다.");
    } catch (error) {
      notify(error.message, "error");
    }
  }

  return {
    api,
    tokens,
    profile,
    isSignedIn,
    authMode,
    setAuthMode,
    authForm,
    setAuthForm,
    authLoading,
    profileForm,
    setProfileForm,
    handleAuthSubmit,
    handleLogout,
    handleUpdateNickname,
    handleUpdatePassword,
  };
}
