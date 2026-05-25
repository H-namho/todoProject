import Toast from "../common/Toast";

export default function AuthScreen({ authMode, setAuthMode, authForm, setAuthForm, loading, onSubmit, toast }) {
  return (
    <main className="auth-shell">
      <section className="auth-panel">
        <div>
          <p className="eyebrow">Memory Practice</p>
          <h1>반복과 오늘 할 일을 한 화면에서 관리하세요</h1>
          <p className="auth-copy">로그인하면 개인 할 일, 반복 일정, 우선순위 필터와 실시간 알림을 바로 사용할 수 있습니다.</p>
        </div>

        <form className="auth-card" onSubmit={onSubmit}>
          <div className="mode-switch" aria-label="인증 모드">
            <button type="button" className={authMode === "signin" ? "active" : ""} onClick={() => setAuthMode("signin")}>
              로그인
            </button>
            <button type="button" className={authMode === "signup" ? "active" : ""} onClick={() => setAuthMode("signup")}>
              회원가입
            </button>
          </div>
          <label>
            아이디
            <input value={authForm.username} onChange={(event) => setAuthForm({ ...authForm, username: event.target.value })} required />
          </label>
          <label>
            비밀번호
            <input
              type="password"
              value={authForm.password}
              onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })}
              required
            />
          </label>
          {authMode === "signup" && (
            <label>
              닉네임
              <input value={authForm.nickname} onChange={(event) => setAuthForm({ ...authForm, nickname: event.target.value })} required />
            </label>
          )}
          <button className="primary-button" disabled={loading}>
            {loading ? "처리 중" : authMode === "signin" ? "로그인" : "가입하고 시작"}
          </button>
        </form>
      </section>
      <Toast toast={toast} />
    </main>
  );
}
