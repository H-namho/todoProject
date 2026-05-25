export default function SettingsPanel({ profileForm, setProfileForm, onUpdateNickname, onUpdatePassword }) {
  return (
    <div className="settings-stack">
      <form className="stack-form" onSubmit={onUpdateNickname}>
        <div>
          <p className="eyebrow">Profile</p>
          <h3>닉네임</h3>
        </div>
        <label>
          닉네임
          <input value={profileForm.nickname} onChange={(event) => setProfileForm({ ...profileForm, nickname: event.target.value })} required />
        </label>
        <button className="primary-button">저장</button>
      </form>
      <form className="stack-form compact" onSubmit={onUpdatePassword}>
        <div>
          <p className="eyebrow">Security</p>
          <h3>비밀번호</h3>
        </div>
        <label>
          현재 비밀번호
          <input
            type="password"
            value={profileForm.nowPassword}
            onChange={(event) => setProfileForm({ ...profileForm, nowPassword: event.target.value })}
            required
          />
        </label>
        <label>
          새 비밀번호
          <input
            type="password"
            value={profileForm.newPassword}
            onChange={(event) => setProfileForm({ ...profileForm, newPassword: event.target.value })}
            minLength={8}
            required
          />
        </label>
        <button className="primary-button">변경</button>
      </form>
    </div>
  );
}
