import Metric from "../common/Metric";

export default function Topbar({ summary, todayCount, highCount }) {
  return (
    <header className="topbar">
      <div>
        <p className="eyebrow">Dashboard</p>
        <h2>오늘의 작업 흐름</h2>
      </div>
      <div className="metric-row">
        <Metric label="전체" value={summary.totalCount} />
        <Metric label="완료" value={summary.completedCount} />
        <Metric label="오늘" value={todayCount} />
        <Metric label="높음" value={highCount} />
      </div>
    </header>
  );
}
