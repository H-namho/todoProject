import { LogOut, Plus, RefreshCw, Settings, User } from "lucide-react";
import SidebarRoutine from "../repeat/SidebarRoutine";

export default function Sidebar({ profile, repeats, activePanel, setActivePanel, onRefreshRepeats, onToggleRepeat, onCompleteRepeat, onLogout }) {
  return (
    <aside className="sidebar">
      <div>
        <p className="eyebrow">Memory Practice</p>
        <h1>Todo Desk</h1>
      </div>
      <div className="profile-box">
        <div className="avatar">
          <User size={20} />
        </div>
        <div>
          <strong>{profile?.nickname ?? "사용자"}</strong>
          <span>{profile?.username}</span>
        </div>
      </div>
      <SidebarRoutine items={repeats} onRefresh={onRefreshRepeats} onToggle={onToggleRepeat} onComplete={onCompleteRepeat} />
      <nav className="nav-list">
        <button className={activePanel === "todo" ? "active" : ""} onClick={() => setActivePanel("todo")}>
          <Plus size={18} /> 할 일 작성
        </button>
        <button className={activePanel === "repeat" ? "active" : ""} onClick={() => setActivePanel("repeat")}>
          <RefreshCw size={18} /> 반복 관리
        </button>
        <button className={activePanel === "settings" ? "active" : ""} onClick={() => setActivePanel("settings")}>
          <Settings size={18} /> 계정 설정
        </button>
      </nav>
      <button className="ghost-button" onClick={onLogout}>
        <LogOut size={18} /> 로그아웃
      </button>
    </aside>
  );
}
