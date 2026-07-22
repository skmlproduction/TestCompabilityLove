#!/usr/bin/env python3
"""Generate docs/design/v2/*.svg editorial romance references for all screen_ids."""
from __future__ import annotations

import csv
import html
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs/design/v2"
CSV = ROOT / "docs/product/screens_catalog.csv"

INK = "#1A1218"
PAPER = "#FFF8F6"
ROSE = "#9F2A4A"
BLUSH = "#F3D9E0"
SECONDARY = "#C45A72"
MUTED = "#6B5E66"
TEAL = "#0F6B63"
TEAL_DARK = "#0A4A45"
HERO_END = "#E8A0B0"

# RU titles for key screens (fallback to screen_id)
TITLES: dict[str, tuple[str, str, str]] = {
    "splash_brand": ("Love Tester", "Проверьте свою любовь", "Совместимость за секунды"),
    "onboarding_welcome": ("Love Tester", "Проверьте свою любовь", "Страница 1 из 4"),
    "onboarding_tests": ("Love Tester", "8 режимов", "Выберите тест"),
    "onboarding_disclaimer": ("Love Tester", "Только для развлечения", "Дисклеймер"),
    "onboarding_protocol": ("Love Tester", "Протокол любви", "Тест №8"),
    "consent_ads_gdpr": ("Реклама и конфиденциальность", "Мы уважаем ваш выбор", "Продолжить"),
    "hub_main": ("Love Tester", "Привет!", "8 режимов — выберите тест"),
    "hub_loading": ("Love Tester", "Загрузка…", ""),
    "love_test_input": ("Тест на любовь", "Кто ваша пара?", "Два имени → процент"),
    "love_test_calculating": ("Расчёт", "Считаем совместимость", ""),
    "love_test_result": ("Результат", "87%", "Сильная связь"),
    "love_test_result_low": ("Результат", "23%", "Ещё есть надежда"),
    "calculator_input": ("Калькулятор любви", "По буквам имён", "Быстрый расчёт"),
    "calculator_result": ("Результат", "74%", "Калькулятор"),
    "pair_input": ("Совместимость пары", "Два имени — одна пара", "Тест №3"),
    "pair_result": ("Результат пары", "74%", "Метрики"),
    "victory_input": ("Победа любви", "Имя → послание", "Тест №4"),
    "victory_result": ("Результат", "Да!", "Победа"),
    "letters_input": ("По буквам", "Буквы имени", "Секретный код"),
    "letters_result": ("Результат", "Код", "Буквы"),
    "zodiac_pick": ("Знаки зодиака", "Два знака", "Космическая пара"),
    "zodiac_result": ("Результат", "78%", "Гороскоп"),
    "wheel_spin": ("Колесо фантазий", "Крутите!", "Тест №7"),
    "wheel_result": ("Результат колеса", "Поцелуй", "Идея для пары"),
    "premium_paywall": ("Premium", "Поддержать разработку", "Навсегда"),
    "premium_thank_you": ("Спасибо!", "Вы с нами", "Premium активен"),
    "settings_main": ("Настройки", "Аккаунт и приложение", ""),
    "share_result_card": ("Поделиться", "87%", "Карточка результата"),
    "error_network": ("Нет сети", "Проверьте соединение", "Повторить"),
    "ad_interstitial_placeholder": ("Реклама", "Placeholder", ""),
    "protocol_input": ("Протокол любви", "Сигналы пары", "Тест №8"),
    "protocol_calculating": ("Протокол", "Анализируем сигналы", ""),
    "protocol_result": ("Результат протокола", "82%", "Protocol complete"),
    "protocol_result_low": ("Результат протокола", "28%", "Осторожный режим"),
}


def lane_colors(screen_id: str) -> tuple[str, str, str]:
    if "protocol" in screen_id:
        return TEAL_DARK, TEAL, "#2A9B90"
    if "zodiac" in screen_id:
        return "#1A1630", "#4A2C6A", ROSE
    if "result_low" in screen_id:
        return "#6B5E66", "#9A8A90", "#EDE0E4"
    return ROSE, SECONDARY, HERO_END


def svg_for(screen_id: str, screen_no: str) -> str:
    title, hero, sub = TITLES.get(screen_id, (screen_id, "", ""))
    c1, c2, c3 = lane_colors(screen_id)
    t = html.escape(title)
    h = html.escape(hero)
    s = html.escape(sub)
    is_result = "result" in screen_id and "share" not in screen_id
    is_input = "input" in screen_id or screen_id in ("zodiac_pick", "wheel_spin")

    hero_block = f"""
  <defs>
    <linearGradient id="bg" x1="0" y1="0" x2="1080" y2="2400" gradientUnits="userSpaceOnUse">
      <stop stop-color="{PAPER}"/>
      <stop offset="1" stop-color="{BLUSH}"/>
    </linearGradient>
    <linearGradient id="hero" x1="72" y1="200" x2="1008" y2="520" gradientUnits="userSpaceOnUse">
      <stop stop-color="{c1}"/>
      <stop offset="0.55" stop-color="{c2}"/>
      <stop offset="1" stop-color="{c3}"/>
    </linearGradient>
  </defs>
  <rect width="1080" height="2400" fill="url(#bg)"/>
  <circle cx="920" cy="180" r="200" fill="{BLUSH}" opacity="0.55"/>
  <circle cx="120" cy="2100" r="260" fill="{BLUSH}" opacity="0.4"/>
  <text x="72" y="140" fill="{MUTED}" font-family="Inter,Roboto,Arial,sans-serif" font-size="22" font-weight="600" letter-spacing="3">ЭКРАН {html.escape(screen_no)} · {html.escape(screen_id.upper())}</text>
"""

    if screen_id == "splash_brand":
        body = f"""
  <text x="540" y="520" text-anchor="middle" fill="{INK}" font-family="Inter,Roboto,Arial,sans-serif" font-size="56" font-weight="900">{t}</text>
  <rect x="120" y="600" width="840" height="520" rx="54" fill="url(#hero)"/>
  <text x="540" y="820" text-anchor="middle" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="48" font-weight="900">{h}</text>
  <text x="540" y="900" text-anchor="middle" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="28" opacity="0.92">{s}</text>
  <rect x="200" y="2000" width="680" height="12" rx="6" fill="{BLUSH}"/>
  <rect x="200" y="2000" width="280" height="12" rx="6" fill="{ROSE}"/>
"""
    elif screen_id == "hub_main":
        body = f"""
  <text x="72" y="200" fill="{INK}" font-family="Inter,Roboto,Arial,sans-serif" font-size="40" font-weight="900">{t}</text>
  <rect x="72" y="260" width="936" height="280" rx="46" fill="url(#hero)"/>
  <text x="120" y="360" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="44" font-weight="900">{h}</text>
  <text x="120" y="430" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="26" opacity="0.92">Когда нужно узнать % любви — начните с главного теста.</text>
  <rect x="72" y="580" width="936" height="100" rx="32" fill="{BLUSH}"/>
  <text x="140" y="640" fill="{ROSE}" font-family="Inter,Roboto,Arial,sans-serif" font-size="28" font-weight="700">Premium — поддержка проекта</text>
  <text x="72" y="760" fill="{INK}" font-family="Inter,Roboto,Arial,sans-serif" font-size="44" font-weight="900">Тесты</text>
  <text x="72" y="820" fill="{MUTED}" font-family="Inter,Roboto,Arial,sans-serif" font-size="28">{s}</text>
  <rect x="72" y="880" width="936" height="200" rx="38" fill="url(#hero)"/>
  <text x="200" y="980" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="32" font-weight="800">Тест на любовь</text>
  <rect x="820" y="940" width="140" height="80" rx="40" fill="#fff"/>
  <text x="890" y="990" text-anchor="middle" fill="{ROSE}" font-family="Inter,Roboto,Arial,sans-serif" font-size="28" font-weight="900">GO</text>
"""
    elif is_result:
        body = f"""
  <text x="540" y="200" text-anchor="middle" fill="{INK}" font-family="Inter,Roboto,Arial,sans-serif" font-size="36" font-weight="800">{t}</text>
  <rect x="72" y="260" width="936" height="720" rx="48" fill="url(#hero)"/>
  <circle cx="540" cy="560" r="150" fill="none" stroke="#fff" stroke-width="18" opacity="0.95"/>
  <text x="540" y="580" text-anchor="middle" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="96" font-weight="900">{h}</text>
  <text x="540" y="640" text-anchor="middle" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="28" opacity="0.9">{s}</text>
  <rect x="72" y="1040" width="936" height="280" rx="38" fill="#fff" stroke="{BLUSH}" stroke-width="2"/>
  <text x="120" y="1140" fill="{INK}" font-family="Inter,Roboto,Arial,sans-serif" font-size="36" font-weight="800">Итог</text>
  <text x="120" y="1210" fill="{MUTED}" font-family="Inter,Roboto,Arial,sans-serif" font-size="28">Editorial result card — один вывод, один CTA.</text>
  <rect x="72" y="2100" width="936" height="112" rx="56" fill="{ROSE}"/>
  <text x="540" y="2170" text-anchor="middle" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="32" font-weight="800">Поделиться</text>
"""
    elif is_input or screen_id.endswith("_pick") or screen_id == "wheel_spin":
        body = f"""
  <text x="540" y="200" text-anchor="middle" fill="{INK}" font-family="Inter,Roboto,Arial,sans-serif" font-size="36" font-weight="800">{t}</text>
  <rect x="72" y="260" width="936" height="280" rx="46" fill="url(#hero)"/>
  <text x="120" y="380" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="40" font-weight="900">{h}</text>
  <text x="120" y="450" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="28" opacity="0.92">{s}</text>
  <rect x="72" y="600" width="936" height="520" rx="38" fill="#fff" stroke="{BLUSH}" stroke-width="2"/>
  <text x="120" y="700" fill="{ROSE}" font-family="Inter,Roboto,Arial,sans-serif" font-size="26" font-weight="700">Поле 1</text>
  <rect x="120" y="740" width="840" height="96" rx="28" fill="{PAPER}" stroke="{ROSE}" stroke-width="3"/>
  <text x="120" y="920" fill="{ROSE}" font-family="Inter,Roboto,Arial,sans-serif" font-size="26" font-weight="700">Поле 2</text>
  <rect x="120" y="960" width="840" height="96" rx="28" fill="{PAPER}" stroke="#D4C4C9" stroke-width="2"/>
  <rect x="72" y="2100" width="936" height="112" rx="56" fill="{c1 if 'protocol' in screen_id else ROSE}"/>
  <text x="540" y="2170" text-anchor="middle" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="32" font-weight="800">Продолжить</text>
"""
    else:
        body = f"""
  <text x="72" y="220" fill="{INK}" font-family="Inter,Roboto,Arial,sans-serif" font-size="40" font-weight="900">{t}</text>
  <rect x="72" y="300" width="936" height="420" rx="54" fill="url(#hero)"/>
  <text x="120" y="460" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="44" font-weight="900">{h}</text>
  <text x="120" y="540" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="28" opacity="0.92">{s}</text>
  <rect x="72" y="800" width="936" height="200" rx="38" fill="#fff" stroke="{BLUSH}" stroke-width="2"/>
  <text x="120" y="910" fill="{MUTED}" font-family="Inter,Roboto,Arial,sans-serif" font-size="28">Editorial card — один блок контента.</text>
  <rect x="72" y="2100" width="936" height="112" rx="56" fill="{ROSE}"/>
  <text x="540" y="2170" text-anchor="middle" fill="#fff" font-family="Inter,Roboto,Arial,sans-serif" font-size="32" font-weight="800">Далее</text>
"""

    return f"""<?xml version="1.0" encoding="UTF-8"?>
<svg width="1080" height="2400" viewBox="0 0 1080 2400" fill="none" xmlns="http://www.w3.org/2000/svg">
{hero_block}{body}
</svg>
"""


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    rows = []
    with CSV.open(encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f):
            sid = row["screen_id"].strip()
            sno = row["screen_no"].strip()
            name = f"screen{sno}_{sid}_v2.svg"
            path = OUT / name
            path.write_text(svg_for(sid, sno), encoding="utf-8")
            rows.append((sid, name))
            print(f"wrote {path.relative_to(ROOT)}")

    readme = ["# Love Tester design v2 — light editorial romance", "", "| screen_id | SVG |", "|-----------|-----|"]
    for sid, name in rows:
        readme.append(f"| `{sid}` | [`{name}`](./{name}) |")
    readme.append("")
    readme.append("Generated by `scripts/generate_v2_design_refs.py`. Tokens: `docs/design/DESIGN_SYSTEM.md`.")
    (OUT / "README.md").write_text("\n".join(readme) + "\n", encoding="utf-8")
    print(f"OK: {len(rows)} SVGs + README")


if __name__ == "__main__":
    main()
