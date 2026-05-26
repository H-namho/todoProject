import { useCallback, useEffect, useState } from "react";
import {
  cancelRepeatToday,
  completeRepeatToday,
  createRepeat,
  deleteRepeat,
  getRepeatCompletionDays,
  getRepeatMonthlyCount,
  getRepeats,
  toggleRepeatActive,
  updateRepeat,
} from "../api/repeatApi";
import { currentMonth, emptyRepeatForm } from "../utils/date";

export default function useRepeats(api, isSignedIn, notify, onCreated) {
  const [repeatForm, setRepeatForm] = useState(emptyRepeatForm());
  const [repeatItems, setRepeatItems] = useState([]);
  const [editingRepeat, setEditingRepeat] = useState(null);
  const [selectedRepeatId, setSelectedRepeatId] = useState(null);
  const [detailMonth, setDetailMonth] = useState(currentMonth());
  const [detail, setDetail] = useState({ loading: false, completedDays: [], monthlyCount: null });

  const loadRepeats = useCallback(async function loadRepeats() {
    if (!isSignedIn) return;
    try {
      const data = await getRepeats(api);
      setRepeatItems(Array.isArray(data) ? data : []);
    } catch (error) {
      notify(error.message, "error");
    }
  }, [api, isSignedIn, notify]);

  const loadDetail = useCallback(async function loadDetail(repeatId, monthValue) {
    if (!repeatId) return;

    setDetail(function setLoading(previous) {
      return { ...previous, loading: true };
    });

    try {
      const result = await Promise.all([
        getRepeatCompletionDays(api, repeatId),
        getRepeatMonthlyCount(api, repeatId, monthValue),
      ]);

      setDetail({
        loading: false,
        completedDays: Array.isArray(result[0]) ? result[0] : [],
        monthlyCount: result[1],
      });
    } catch (error) {
      setDetail({ loading: false, completedDays: [], monthlyCount: null });
      notify(error.message, "error");
    }
  }, [api, notify]);

  useEffect(function fetchRepeatsAfterAuthentication() {
    if (!isSignedIn) {
      setRepeatItems([]);
      setEditingRepeat(null);
      setSelectedRepeatId(null);
      setDetail({ loading: false, completedDays: [], monthlyCount: null });
      return;
    }
    loadRepeats();
  }, [isSignedIn, loadRepeats]);

  async function submitRepeat(event) {
    event.preventDefault();

    if (repeatForm.repeatType === "WEEKLY" && repeatForm.dayOfWeek.length === 0) {
      notify("매주 반복은 수행할 요일을 선택해야 합니다.", "error");
      return;
    }

    try {
      if (editingRepeat) {
        const clearEndDate = Boolean(editingRepeat.endDate) && !repeatForm.endDate;
        await updateRepeat(api, editingRepeat.repeatId, {
          repeatType: repeatForm.repeatType,
          dayOfWeekSet: repeatForm.repeatType === "WEEKLY" ? repeatForm.dayOfWeek : [],
          startDate: repeatForm.startDate,
          endDate: clearEndDate ? null : repeatForm.endDate || null,
          clearEndDate,
        });
        notify("반복 일정을 수정했습니다.");
        setEditingRepeat(null);
      } else {
        await createRepeat(api, {
          title: repeatForm.title.trim(),
          memo: repeatForm.memo.trim(),
          repeatType: repeatForm.repeatType,
          dayOfWeek: repeatForm.repeatType === "WEEKLY" ? repeatForm.dayOfWeek : [],
          startDate: repeatForm.startDate,
          endDate: repeatForm.endDate || null,
        });
        notify("반복 할 일을 만들었습니다.");
        if (onCreated) {
          onCreated();
        }
      }

      setRepeatForm(emptyRepeatForm());
      await loadRepeats();
      if (selectedRepeatId) {
        await loadDetail(selectedRepeatId, detailMonth);
      }
    } catch (error) {
      notify(error.message, "error");
    }
  }

  function startEdit(item) {
    setEditingRepeat(item);
    setRepeatForm({
      title: item.title ?? "",
      memo: item.memo ?? "",
      repeatType: item.repeatType ?? "DAILY",
      dayOfWeek: Array.isArray(item.dayOfWeek) ? item.dayOfWeek : [],
      startDate: item.startDate ?? "",
      endDate: item.endDate ?? "",
    });
  }

  function cancelEdit() {
    setEditingRepeat(null);
    setRepeatForm(emptyRepeatForm());
  }

  async function toggleRepeat(repeatId) {
    const target = repeatItems.find(function findTarget(item) {
      return item.repeatId === repeatId;
    });
    if (!target) return;

    const nextActive = !target.active;
    setRepeatItems(function applyActive(items) {
      return items.map(function updateItem(item) {
        return item.repeatId === repeatId ? { ...item, active: nextActive } : item;
      });
    });

    try {
      await toggleRepeatActive(api, repeatId);
      notify(nextActive ? "반복 일정을 다시 활성화했습니다." : "반복 일정을 중지했습니다.");
    } catch (error) {
      setRepeatItems(function restoreActive(items) {
        return items.map(function restoreItem(item) {
          return item.repeatId === repeatId ? { ...item, active: target.active } : item;
        });
      });
      notify(error.message, "error");
    }
  }

  async function completeRepeat(repeatId) {
    const target = repeatItems.find(function findTarget(item) {
      return item.repeatId === repeatId;
    });
    if (!target) return;

    const nextCompleted = !target.completedToday;
    setRepeatItems(function applyCompletion(items) {
      return items.map(function updateItem(item) {
        return item.repeatId === repeatId ? { ...item, completedToday: nextCompleted } : item;
      });
    });

    try {
      if (nextCompleted) {
        await completeRepeatToday(api, repeatId);
        notify("오늘의 반복 할 일을 완료했습니다.");
      } else {
        await cancelRepeatToday(api, repeatId);
        notify("오늘의 완료를 취소했습니다.");
      }

      if (selectedRepeatId === repeatId) {
        await loadDetail(repeatId, detailMonth);
      }
    } catch (error) {
      setRepeatItems(function restoreCompletion(items) {
        return items.map(function restoreItem(item) {
          return item.repeatId === repeatId ? { ...item, completedToday: target.completedToday } : item;
        });
      });
      notify(error.message, "error");
    }
  }

  async function removeRepeat(repeatId) {
    if (!window.confirm("이 루틴과 완료 기록을 모두 삭제할까요?")) return;

    try {
      await deleteRepeat(api, repeatId);
      notify("반복 일정을 삭제했습니다.");
      if (editingRepeat?.repeatId === repeatId) {
        cancelEdit();
      }
      if (selectedRepeatId === repeatId) {
        setSelectedRepeatId(null);
        setDetail({ loading: false, completedDays: [], monthlyCount: null });
      }
      await loadRepeats();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function selectRepeat(repeatId) {
    setSelectedRepeatId(repeatId);
    await loadDetail(repeatId, detailMonth);
  }

  async function changeDetailMonth(monthValue) {
    setDetailMonth(monthValue);
    if (selectedRepeatId) {
      await loadDetail(selectedRepeatId, monthValue);
    }
  }

  return {
    repeatForm,
    setRepeatForm,
    repeatItems,
    editingRepeat,
    selectedRepeatId,
    detailMonth,
    detail,
    loadRepeats,
    submitRepeat,
    startEdit,
    cancelEdit,
    toggleRepeat,
    completeRepeat,
    removeRepeat,
    selectRepeat,
    changeDetailMonth,
  };
}
