# MCP Gateway UI (1-19)

Blade Runner Neon 美学的 MCP Gateway 管理后台。位于 monorepo 根 `ai-mcp-gateway-ui/`,Maven 不扫,pnpm + Vite 独立管理。

## 启动

```bash
cd ai-mcp-gateway-ui
pnpm install
pnpm dev          # http://localhost:5173
```

Mock 模式默认开启 (`VITE_MOCK=true`),无需后端即可联调。

## 技术栈

- React 18 + Vite 6 + TypeScript 5
- Tailwind CSS v4 (CSS variables 主题)
- TanStack Query 5 (数据获取)
- React Hook Form + zod (表单 + 校验)
- Zustand (状态,持久化)
- Framer Motion (动画)
- Three.js + @react-three/fiber (Galaxy widget - 1-19 spec)
- MSW (Mock 拦截)
- Lucide React (图标)

## 路由

| 路径 | 页面 |
|---|---|
| `/login` | 登录 (admin/password123) |
| `/admin` | 控制台 + Galaxy widget |
| `/admin/gateways` | 网关列表 + CRUD |
| `/admin/configs` | 基础配置 |
| `/admin/tools` | 工具配置 |
| `/admin/protocols` | 协议配置 |
| `/admin/auth` | 认证配置 |

## API 端点

```
POST /api-gateway/admin/save_gateway_config
POST /api-gateway/admin/save_gateway_tool_config
POST /api-gateway/admin/save_gateway_protocol
POST /api-gateway/admin/save_gateway_auth
POST /api-gateway/admin/query_gateway_config_list
```

## 目录

```
src/
├── api/           client + endpoints + schemas + hooks
├── components/    AppLayout 等通用组件
├── pages/         6 页面 + 登录
├── stores/        zustand (auth)
├── theme/         globals.css (Blade Runner Neon 主题)
├── widgets/
│   └── Galaxy/    3D 银河 widget + CSS fallback
├── mocks/         MSW handlers + fixtures
└── main.tsx       入口
```

## 主题

Blade Runner Neon - 深紫底 + 洋红/青霓虹 + 扫描线 + CRT 暗角。
字体:Orbitron (display) + Share Tech Mono (UI) + Noto Sans JP (中文) + JetBrains Mono (mono)。

切换亮/暗主题预留 CSS variables,默认 dark。

## 开发

```bash
pnpm dev           # 启动 dev server
pnpm typecheck     # tsc -b --noEmit
pnpm test          # vitest
pnpm test:e2e      # playwright
pnpm build         # 生产构建
```
