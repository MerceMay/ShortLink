import {
  _ as C,
  g as V,
  u as M,
  r as p,
  o as U,
  m as u,
  c as s,
  d as B,
  e as E,
  h as t,
  w as n,
  f as e,
  i as m,
  t as H,
  q as N,
  v as R,
  x as T,
  E as q,
  p as A,
  k as P,
} from "./index-5f00e820.js";
const l = (c) => (A("data-v-a730f700"), (c = c()), P(), c),
  D = { class: "common-layout" },
  K = { class: "header" },
  $ = { style: { display: "flex", "align-items": "center" } },
  j = l(() =>
    e(
      "a",
      {
        class: "link-span",
        style: { "text-decoration": "none" },
        target: "_blank",
        href: "https://nageoffer.com/shortlink/",
      },
      "官方文档",
      -1
    )
  ),
  z = l(() =>
    e(
      "a",
      {
        class: "link-span",
        style: { "text-decoration": "none" },
        target: "_blank",
        href: "https://nageoffer.com/group/",
      },
      "加沟通群",
      -1
    )
  ),
  F = l(() =>
    e(
      "a",
      {
        class: "link-span",
        style: { "text-decoration": "none" },
        target: "_blank",
        href: "https://nageoffer.com/shortlink/video/",
      },
      "🔥视频教程",
      -1
    )
  ),
  G = l(() =>
    e(
      "a",
      {
        class: "link-span",
        style: { "text-decoration": "none" },
        target: "_blank",
        href: "http://shortlink.magestack.cn",
      },
      "演示环境",
      -1
    )
  ),
  J = { class: "block" },
  O = { class: "name-span", style: { "text-decoration": "none" } },
  Q = { class: "content-box" },
  W = {
    __name: "HomeIndex",
    setup(c) {
      const { proxy: h } = V(),
        _ = h.$API,
        r = M();
      p("https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png");
      const f = () => {
          r.push("/home/account");
        },
        g = async () => {
          const o = N(),
            a = u();
          await _.user.logout({ token: o, username: a }),
            R(),
            T(),
            localStorage.removeItem("token"),
            localStorage.removeItem("username"),
            r.push("/login"),
            q.success("成功退出！");
        },
        k = () => {
          r.push("/home/space");
        },
        d = p("");
      U(async () => {
        const o = u();
        await _.user.queryUserInfo(o), (d.value = v(o, 8));
      });
      const v = (o, a) => (o.length > a ? o.slice(0, a) + "..." : o);
      return (o, a) => {
        const i = s("el-dropdown-item"),
          y = s("el-dropdown-menu"),
          x = s("el-dropdown"),
          w = s("el-header"),
          I = s("RouterView"),
          S = s("el-main"),
          b = s("el-container");
        return (
          B(),
          E("div", D, [
            t(b, null, {
              default: n(() => [
                t(
                  w,
                  { height: "54px", style: { padding: "0" } },
                  {
                    default: n(() => [
                      e("div", K, [
                        e(
                          "div",
                          { onClick: k, class: "logo" },
                          "拿个offer-SaaS短链接"
                        ),
                        e("div", $, [
                          j,
                          z,
                          F,
                          G,
                          t(x, null, {
                            dropdown: n(() => [
                              t(y, null, {
                                default: n(() => [
                                  t(
                                    i,
                                    { onClick: f },
                                    { default: n(() => [m("个人信息")]), _: 1 }
                                  ),
                                  t(
                                    i,
                                    { divided: "", onClick: g },
                                    { default: n(() => [m("退出")]), _: 1 }
                                  ),
                                ]),
                                _: 1,
                              }),
                            ]),
                            default: n(() => [
                              e("div", J, [e("span", O, H(d.value), 1)]),
                            ]),
                            _: 1,
                          }),
                        ]),
                      ]),
                    ]),
                    _: 1,
                  }
                ),
                t(
                  S,
                  { style: { padding: "0" } },
                  {
                    default: n(() => [
                      e("div", Q, [t(I, { class: "content-space" })]),
                    ]),
                    _: 1,
                  }
                ),
              ]),
              _: 1,
            }),
          ])
        );
      };
    },
  },
  Y = C(W, [["__scopeId", "data-v-a730f700"]]);
export { Y as default };
