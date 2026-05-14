#!/usr/bin/env python3
"""生成简历 PDF"""

from fpdf import FPDF
from fpdf.enums import XPos, YPos
import os

# ============================================================
# 配置
# ============================================================
OUTPUT_PATH = r"D:\edg下载\直聘简历-未命名.pdf"
FONT_DIR = r"C:\Windows\Fonts"

# 姓名和联系方式（请根据实际情况修改）
NAME = "靳子鑫"
PHONE = "请填写手机号"
EMAIL = "请填写邮箱"
AGE = "请填写年龄"
LOCATION = "请填写城市"
YEARS_OF_EXP = "请填写工作年限"

# ============================================================
# PDF 生成
# ============================================================

class ResumePDF(FPDF):
    def __init__(self):
        super().__init__('P', 'mm', 'A4')
        # 注册中文字体
        self.add_font('hei', '', os.path.join(FONT_DIR, 'simhei.ttf'))
        self.add_font('sun', '', os.path.join(FONT_DIR, 'simsun.ttc'))
        self.add_font('kai', '', os.path.join(FONT_DIR, 'simkai.ttf'))
        self.add_font('fang', '', os.path.join(FONT_DIR, 'simfang.ttf'))
        self.add_font('msyh', '', os.path.join(FONT_DIR, 'msyh.ttc'))
        self.add_font('msyhbd', '', os.path.join(FONT_DIR, 'msyhbd.ttc'))

    def section_title(self, title):
        """章节标题"""
        self.set_fill_color(64, 64, 64)
        self.set_text_color(255, 255, 255)
        self.set_font('hei', '', 13)
        self.cell(0, 8, f'    {title}', fill=True, new_x=XPos.LMARGIN, new_y=YPos.NEXT)
        self.ln(2)

    def sub_title(self, text, right_text=''):
        """子标题 - 左侧加粗 + 右侧文字"""
        self.set_text_color(0, 0, 0)
        self.set_font('hei', '', 11)
        if right_text:
            self.cell(100, 6, f'  {text}')
            self.cell(0, 6, right_text, align='R')
        else:
            self.cell(0, 6, f'  {text}')
        self.ln()

    def body_line(self, text, indent=4, color=(51, 51, 51)):
        """正文行"""
        self.set_text_color(*color)
        self.set_font('fang', '', 10)
        self.set_x(self.l_margin + indent)
        self.multi_cell(self.w - self.l_margin - self.r_margin - indent * 2, 5.5, text)

    def dot_point(self, text):
        """带圆点的要点"""
        self.body_line(f'> {text}', indent=6)

    def tech_tag(self, text):
        """技术标签"""
        self.set_font('msyh', '', 9)
        self.set_text_color(64, 64, 64)
        return self.get_string_width(text) + 6

    def header(self):
        pass

    def footer(self):
        pass


pdf = ResumePDF()
pdf.set_auto_page_break(True, 15)
pdf.add_page()
pdf.set_left_margin(15)
pdf.set_right_margin(15)

# ============================================================
# 姓名 + 个人信息
# ============================================================
pdf.ln(4)
pdf.set_text_color(0, 0, 0)
pdf.set_font('hei', '', 26)
pdf.cell(0, 10, NAME, align='C', new_x=XPos.LMARGIN, new_y=YPos.NEXT)
pdf.ln(3)

# 联系方式一行
pdf.set_font('msyh', '', 10)
pdf.set_text_color(100, 100, 100)
info_line = f'{PHONE}  |  {EMAIL}  |  {AGE}  |  {LOCATION}  |  {YEARS_OF_EXP}年工作经验'
pdf.cell(0, 6, info_line, align='C', new_x=XPos.LMARGIN, new_y=YPos.NEXT)
pdf.ln(6)

# ============================================================
# 专业技能
# ============================================================
pdf.section_title('专业技能')

skills = [
    ('后端开发', '熟练掌握 Java，熟悉 Spring Boot、Spring Security、Spring Data JPA 框架；能独立搭建 RESTful API 服务；了解 JWT/OAuth2 认证授权机制'),
    ('数据库', '熟练使用 MySQL，具备表结构设计、SQL 优化经验；熟悉 JPA/Hibernate ORM 框架'),
    ('前端开发', '熟练使用 Vue 3（Composition API）+ Element Plus 构建后台管理系统；了解 Vue Router、Pinia 状态管理、Axios 拦截器封装'),
    ('工程化', '熟练使用 Maven 依赖管理、Git 版本控制；了解 Vite 构建工具与前后端分离开发模式'),
]

for title, desc in skills:
    pdf.set_text_color(0, 0, 0)
    pdf.set_font('hei', '', 10)
    pdf.cell(pdf.get_string_width(f'{title}：') + 2, 5.5, f'{title}：')
    pdf.set_font('fang', '', 10)
    pdf.set_text_color(51, 51, 51)
    pdf.multi_cell(pdf.w - pdf.l_margin - pdf.r_margin - pdf.get_string_width(f'{title}：') - 4, 5.5, desc)

pdf.ln(2)

# ============================================================
# 项目经历
# ============================================================
pdf.section_title('项目经历')

# --- 项目 1：云电商管理系统 ---
pdf.sub_title('云电商管理系统', '2024.04 - 2024.05')
pdf.ln(1)
pdf.body_line('独立设计并开发的全栈电商后台管理系统，涵盖商品管理、分类管理、订单管理、用户管理和数据仪表盘五大核心模块，采用前后端分离架构，支持 JWT 无状态认证与细粒度权限控制。')
pdf.ln(1)

pdf.body_line('技术栈：', indent=4, color=(0, 0, 0))
pdf.body_line('Java 21 / Spring Boot 2.7 / Spring Security / Spring Data JPA / MySQL 8.0 / Vue 3 / Element Plus / Vite 5 / Pinia / Axios',
              indent=6, color=(80, 80, 80))
pdf.ln(1)

points_project1 = [
    '后端：基于 Spring Boot 搭建 RESTful API，使用统一响应体 ApiResponse<T> 封装接口返回值；通过 Spring Security + JWT 实现无状态认证，自定义 JwtAuthFilter 解析 Token，BCrypt 加密密码；配置 CORS 跨域策略支持前后端分离。',
    '数据层：设计 Product、Order、OrderItem、Category、User 核心实体模型（JPA Entity），使用 @CreationTimestamp/@UpdateTimestamp 自动记录时间戳；基于 JPA Specification 实现商品多条件动态组合查询（关键字模糊搜索 + 分类筛选 + 分页排序），避免硬编码 SQL。',
    '前端：使用 Vue 3 Composition API（<script setup>）开发，封装 Axios 请求/响应拦截器 — 请求侧自动挂载 JWT Token，响应侧统一处理 401 过期跳转登录；Vue Router 4 配置路由懒加载 + 导航守卫（未登录拦截）；Element Plus 构建仪表盘统计卡片、CRUD 表单（含表单校验）、订单列表等界面。',
    '工程化：Maven 多模块管理依赖并生成可执行 JAR；编写 DataInitializer 实现应用启动时自动初始化测试数据，加速开发调试。',
]

for pt in points_project1:
    pdf.dot_point(pt)

pdf.ln(3)

# ============================================================
# 工作经历 / 教育背景
# ============================================================
pdf.section_title('教育背景')

pdf.sub_title('请填写学校名称', '请填写时间')
pdf.body_line('专业：请填写专业  |  学历：请填写学历')
pdf.ln(1)

# ============================================================
# 自我评价（可选）
# ============================================================
pdf.section_title('自我评价')

pdf.body_line('具备良好的自学能力和问题排查能力，能够从零搭建前后端分离项目；注重代码规范与工程化实践；对技术保有好奇心，乐于探索新技术并将其应用于实际项目中。')

# ============================================================
# 输出
# ============================================================
pdf.output(OUTPUT_PATH)
print(f'简历已生成: {OUTPUT_PATH}')
