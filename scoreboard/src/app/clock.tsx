"use client";
import React, { useEffect, useState } from "react";
export default function Clock() {
  const [hours, setHours] = useState("");
  const [minutes, setMinutes] = useState("");
  const [seconds, setSeconds] = useState("");

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
