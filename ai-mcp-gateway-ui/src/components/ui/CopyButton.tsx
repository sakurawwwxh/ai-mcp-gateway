import { useState } from 'react';
import { Copy, Check } from 'lucide-react';
import { Button } from './Button';

interface Props {
  value: string;
  label?: string;
}

export function CopyButton({ value, label = '复制' }: Props) {
  const [copied, setCopied] = useState(false);

  async function copy() {
    try {
      await navigator.clipboard.writeText(value);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    } catch {
      const ta = document.createElement('textarea');
      ta.value = value;
      ta.style.position = 'fixed';
      ta.style.left = '-9999px';
      document.body.appendChild(ta);
      ta.select();
      try { document.execCommand('copy'); } catch { /* noop */ }
      document.body.removeChild(ta);
      setCopied(true);
      setTimeout(() => setCopied(false), 1500);
    }
  }

  return (
    <Button
      type="button"
      variant="secondary"
      size="sm"
      onClick={copy}
      leftIcon={copied ? <Check size={14} className="text-emerald-600" /> : <Copy size={14} />}
    >
      {copied ? '已复制' : label}
    </Button>
  );
}
