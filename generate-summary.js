const fs = require("fs");
const {
  Document, Packer, Paragraph, TextRun,
  AlignmentType, HeadingLevel, BorderStyle,
} = require("docx");

// 实训总结内容
const title = `广西科技大学计算机学院实习实训总结`;

const description = `实训总结是实训项目的重要组成部分，是实训学生必须完成的实训科目。实训总结由实训学生本人完成。实训总结包括专业技能实训、企业文化感受、团队精神训练、职业道德培养、对实训的批评或建议等内容。字数要求：600-2000字。`;

const studentInfo = `姓名：何迈         专业班级：软件231           学号：202300406022`;

// 正文内容（约1000字）
const content = [
  ``,
  `一、专业技能实训`,
  ``,
  `本次实训项目为\u201C医疗管家健康管理系统\u201D（YLGJ），是一个面向体检中心的健康管理平台。在为期两周的实训中，我深入参与了项目的后端开发工作，主要使用了Spring Boot框架搭建RESTful API服务，通过MyBatis Plus实现数据持久层操作，并基于MySQL数据库设计了15张业务表来支撑系统的完整业务流程。`,
  ``,
  `在技术层面，我掌握了Spring Boot的自动配置原理、MyBatis Plus的分页插件与条件构造器的使用、以及RBAC权限模型的实现方法。此外，我还参与了系统与DeepSeek大模型的集成工作，通过调用DeepSeek API实现了AI智能健康助手功能，这让我对大型语言模型的应用有了直观的认识。在移动端开发方面，我使用Vue.js结合Axios完成了前端数据交互，并利用Element UI组件库提升了界面的一致性和用户体验。通过该项目，我还学习了使用Apache POI进行Excel报表的导入导出、Quartz定时任务的配置、以及Redis缓存的应用，整体技术栈得到了全面的锻炼。`,
  ``,
  `二、企业文化感受`,
  ``,
  `实训过程中，指导老师按照企业级项目开发的标准来要求我们，从需求分析、数据库设计、接口文档编写到代码实现和测试，完整地模拟了软件公司的开发流程。我们使用了Git进行版本控制，通过每日站会同步进度，这种类似敏捷开发的工作方式让我提前感受到了互联网企业的工作节奏和协作文化。指导老师强调的\u201C先设计后编码\u201D、\u201C注重代码规范\u201D和\u201C持续集成\u201D等理念，让我认识到在学校学习和在企业工作之间的差异\u2014\u2014企业更注重代码的可维护性、团队协作效率和产品的交付质量。`,
  ``,
  `三、团队精神训练`,
  ``,
  `本项目由7人团队协作完成，我担任后端开发角色。在团队合作中，我深刻体会到沟通的重要性。项目初期，由于前后端接口定义不够清晰，导致联调时出现了大量返工。经过团队讨论后，我们明确了RESTful接口风格，并统一了接口返回结构与错误提示规范（统一的Result封装），大大提高了协作效率。此外，在面对项目进度压力时，团队成员互相支持、主动分担任务，这种团结互助的精神是项目能够最终顺利完成的关键。通过这次实训，我学会了如何在团队中定位自己的角色、如何有效地进行技术沟通、以及如何协调个人进度与团队目标的一致性。`,
  ``,
  `四、职业道德培养`,
  ``,
  `医疗健康管理系统涉及用户的个人健康数据和隐私信息，这对系统的安全性和数据保护提出了较高的要求。在开发过程中，指导老师特别强调了数据安全意识：密码必须加密存储、敏感接口需要做权限校验、用户数据的展示需要脱敏处理等。这让我认识到作为软件开发者，不仅要追求技术上的精湛，更要具备职业道德和社会责任感。我们编写的每一行代码都可能影响到用户的切身利益，因此必须严谨对待，不能有任何侥幸心理。`,
  ``,
  `五、对实训的批评与建议`,
  ``,
  `本次实训整体收获很大，但也有几点改进建议：第一，实训前期可以增加更多关于项目架构设计的讲解，帮助同学们更快地进入开发状态；第二，建议引入自动化测试环节，让同学们养成编写单元测试的习惯；第三，可以适当延长实训周期或精简功能范围，让同学们有更充足的时间深入理解每个模块的实现细节，而非仅仅追求功能的数量；第四，建议增加代码评审（Code Review）环节，让同学们相互学习优秀的编码实践。总体而言，这次实训是一次非常宝贵的实践经历，让我在技术能力、团队协作和职业素养等方面都得到了显著提升。`,
];

// 构建文档
const children = [];

// 标题
children.push(
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { line: 360, lineRule: "auto" },
    children: [
      new TextRun({ text: title, bold: true, size: 32, font: "宋体" }),
    ],
  })
);

// 空行
children.push(new Paragraph({ spacing: { line: 360 }, children: [] }));

// 说明文字
children.push(
  new Paragraph({
    spacing: { line: 360, lineRule: "auto" },
    indent: { firstLine: 435 },
    children: [
      new TextRun({ text: description, size: 24, font: "宋体" }),
    ],
  })
);

// 空行
children.push(new Paragraph({ spacing: { line: 360 }, children: [] }));

// 学生信息 - 居中
children.push(
  new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { line: 360, lineRule: "auto" },
    children: [
      new TextRun({ text: studentInfo, size: 24, font: "宋体" }),
    ],
  })
);

// 分隔线
children.push(
  new Paragraph({
    spacing: { before: 200, after: 200 },
    border: {
      bottom: { style: BorderStyle.SINGLE, size: 6, color: "000000", space: 1 },
    },
    children: [],
  })
);

// 正文内容
const sectionHeadings = [`一、专业技能实训`, `二、企业文化感受`, `三、团队精神训练`, `四、职业道德培养`, `五、对实训的批评与建议`];

for (const line of content) {
  if (line === ``) {
    children.push(new Paragraph({ spacing: { line: 360, lineRule: "auto" }, children: [] }));
  } else if (sectionHeadings.includes(line)) {
    // 小标题
    children.push(
      new Paragraph({
        spacing: { line: 360, lineRule: "auto", before: 200 },
        children: [
          new TextRun({ text: line, bold: true, size: 28, font: "宋体" }),
        ],
      })
    );
  } else {
    // 正文
    children.push(
      new Paragraph({
        spacing: { line: 360, lineRule: "auto" },
        indent: { firstLine: 480 },
        children: [
          new TextRun({ text: line, size: 24, font: "宋体" }),
        ],
      })
    );
  }
}

const doc = new Document({
  styles: {
    default: {
      document: {
        run: { font: "宋体", size: 24 },
      },
    },
  },
  sections: [
    {
      properties: {
        page: {
          size: {
            width: 11906,
            height: 16838,
          },
          margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 },
        },
      },
      children,
    },
  ],
});

const outputPath = `C:\\Users\\1912\\Desktop\\计算机学院实训总结_何迈.docx`;
Packer.toBuffer(doc).then((buffer) => {
  fs.writeFileSync(outputPath, buffer);
  console.log(`文档已生成: ` + outputPath);
  console.log(`文件大小: ` + (buffer.length / 1024).toFixed(1) + ` KB`);
});
