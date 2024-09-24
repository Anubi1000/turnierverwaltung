"use client";
import { useEffect, useState } from "react";

export function Clock() {
  const [hours, setHours] = useState("00");
  const [minutes, setMinutes] = useState("00");
  const [seconds, setSeconds] = useState("00");

  useEffect(() => {
    setInterval(() => {
      const date = new Date();
      const h = date.getHours().toString().padStart(2, "0");
      const m = date.getMinutes().toString().padStart(2, "0");
      const s = date.getSeconds().toString().padStart(2, "0");
      setHours(h);
      setMinutes(m);
      setSeconds(s);
    }, 1000);
  });
  return (
    <p>
      {hours}:{minutes}:{seconds}
    </p>
  );
}
