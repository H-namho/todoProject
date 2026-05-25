import { useCallback, useEffect, useState } from "react";
import { completeRepeatToday, createRepeat, getRepeats, toggleRepeatActive } from "../api/repeatApi";
import { emptyRepeatForm } from "../utils/date";

export default function useRepeats(api, isSignedIn, notify, onCreated) {
  const [repeatForm, setRepeatForm] = useState(emptyRepeatForm());
  const [repeatItems, setRepeatItems] = useState([]);

  const loadRepeats = useCallback(async () => {
    if (!isSignedIn) return;
    try {
      const data = await getRepeats(api);
      setRepeatItems(Array.isArray(data) ? data : []);
    } catch (error) {
      notify(error.message, "error");
    }
  }, [api, isSignedIn, notify]);

  useEffect(() => {
    if (!isSignedIn) {
      setRepeatItems([]);
      return;
    }
    loadRepeats();
  }, [isSignedIn, loadRepeats]);

  async function submitRepeat(event) {
    event.preventDefault();
    try {
      await createRepeat(api, {
        title: repeatForm.title.trim(),
        memo: repeatForm.memo.trim(),
        repeatType: repeatForm.repeatType,
        dayOfWeek: repeatForm.repeatType === "WEEKLY" ? repeatForm.dayOfWeek : [],
        startDate: repeatForm.startDate,
        endDate: repeatForm.endDate || null,
      });
      setRepeatForm(emptyRepeatForm());
      notify("반복 할 일을 만들었습니다.");
      loadRepeats();
      onCreated?.();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function toggleRepeat(repeatId) {
    const target = repeatItems.find((item) => item.repeatId === repeatId);
    if (!target) return;
    const nextActive = !target.active;
    setRepeatItems((items) => items.map((item) => (item.repeatId === repeatId ? { ...item, active: nextActive } : item)));
    try {
      await toggleRepeatActive(api, repeatId);
      notify(nextActive ? "반복 일정을 다시 활성화했습니다." : "반복 일정을 중지했습니다.");
    } catch (error) {
      setRepeatItems((items) => items.map((item) => (item.repeatId === repeatId ? { ...item, active: target.active } : item)));
      notify(error.message, "error");
    }
  }

  async function completeRepeat(repeatId) {
    const target = repeatItems.find((item) => item.repeatId === repeatId);
    if (!target || target.completedToday) return;
    setRepeatItems((items) => items.map((item) => (item.repeatId === repeatId ? { ...item, completedToday: true } : item)));
    try {
      await completeRepeatToday(api, repeatId);
      notify("오늘의 반복 할 일을 완료했습니다.");
    } catch (error) {
      setRepeatItems((items) => items.map((item) => (item.repeatId === repeatId ? { ...item, completedToday: false } : item)));
      notify(error.message, "error");
    }
  }

  return { repeatForm, setRepeatForm, repeatItems, loadRepeats, submitRepeat, toggleRepeat, completeRepeat };
}
