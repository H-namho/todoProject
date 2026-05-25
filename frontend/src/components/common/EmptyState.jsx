import { CalendarDays } from "lucide-react";

export default function EmptyState({ loading }) {
  return (
    <div className="empty-state">
      <CalendarDays size={34} />
      <strong>{loading ? "불러오는 중" : "표시할 할 일이 없습니다"}</strong>
      <span>{loading ? "잠시만 기다려주세요." : "필터를 바꾸거나 새 할 일을 추가하세요."}</span>
    </div>
  );
}
