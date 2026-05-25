import { useCallback, useState } from "react";

export default function useToast() {
  const [toast, setToast] = useState(null);

  const notify = useCallback((message, type = "ok") => {
    setToast({ message, type });
    window.setTimeout(() => setToast(null), 2800);
  }, []);

  return { toast, notify };
}
