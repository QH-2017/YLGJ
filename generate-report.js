const { Document, Packer, Paragraph, TextRun, ImageRun, Table, TableRow, TableCell,
        AlignmentType, BorderStyle, WidthType, PageBreak } = require('docx');
const fs = require('fs');
const path = require('path');

const screenshotsDir = path.join(__dirname, 'ppt_screenshots');

function readImage(filename) {
    return fs.readFileSync(path.join(screenshotsDir, filename));
}

function imageWithCaption(filename, caption, width, height) {
    return [
        new Paragraph({
            alignment: AlignmentType.CENTER,
            spacing: { before: 200, after: 100 },
            children: [
                new ImageRun({
                    type: "png",
                    data: readImage(filename),
                    transformation: { width: width || 480, height: height || 270 },
                    altText: { title: caption, description: caption, name: filename },
                }),
            ],
        }),
        new Paragraph({
            alignment: AlignmentType.CENTER,
            spacing: { after: 200 },
            children: [
                new TextRun({ text: caption, font: "\u5B8B\u4F53", size: 18, italics: true, color: "666666" }),
            ],
        }),
    ];
}

function sectionTitle(text) {
    return new Paragraph({
        spacing: { before: 300, after: 200 },
        children: [
            new TextRun({ text: text, font: "\u9ED1\u4F53", size: 21, bold: true }),
        ],
    });
}

function bodyText(text) {
    return new Paragraph({
        spacing: { after: 120, line: 360 },
        indent: { firstLine: 420 },
        children: [
            new TextRun({ text: text, font: "\u5B8B\u4F53", size: 21 }),
        ],
    });
}

function subTitle(text) {
    return new Paragraph({
        spacing: { before: 200, after: 100 },
        children: [
            new TextRun({ text: text, font: "\u9ED1\u4F53", size: 21, bold: true }),
        ],
    });
}

function twoImagesRow(img1, cap1, img2, cap2) {
    const border = { style: BorderStyle.NONE, size: 0 };
    const borders = { top: border, bottom: border, left: border, right: border };
    return new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [4513, 4513],
        rows: [
            new TableRow({
                children: [
                    new TableCell({
                        borders,
                        width: { size: 4513, type: WidthType.DXA },
                        verticalAlign: "center",
                        children: [
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new ImageRun({
                                        type: "png",
                                        data: readImage(img1),
                                        transformation: { width: 280, height: 190 },
                                        altText: { title: cap1, description: cap1, name: img1 },
                                    }),
                                ],
                            }),
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                spacing: { after: 100 },
                                children: [
                                    new TextRun({ text: cap1, font: "\u5B8B\u4F53", size: 18, italics: true, color: "666666" }),
                                ],
                            }),
                        ],
                    }),
                    new TableCell({
                        borders,
                        width: { size: 4513, type: WidthType.DXA },
                        verticalAlign: "center",
                        children: [
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new ImageRun({
                                        type: "png",
                                        data: readImage(img2),
                                        transformation: { width: 280, height: 190 },
                                        altText: { title: cap2, description: cap2, name: img2 },
                                    }),
                                ],
                            }),
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                spacing: { after: 100 },
                                children: [
                                    new TextRun({ text: cap2, font: "\u5B8B\u4F53", size: 18, italics: true, color: "666666" }),
                                ],
                            }),
                        ],
                    }),
                ],
            }),
        ],
    });
}

function threeImagesRow(img1, cap1, img2, cap2, img3, cap3) {
    const border = { style: BorderStyle.NONE, size: 0 };
    const borders = { top: border, bottom: border, left: border, right: border };
    return new Table({
        width: { size: 9026, type: WidthType.DXA },
        columnWidths: [3008, 3009, 3009],
        rows: [
            new TableRow({
                children: [
                    new TableCell({
                        borders,
                        width: { size: 3008, type: WidthType.DXA },
                        verticalAlign: "center",
                        children: [
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new ImageRun({
                                        type: "png",
                                        data: readImage(img1),
                                        transformation: { width: 200, height: 140 },
                                        altText: { title: cap1, description: cap1, name: img1 },
                                    }),
                                ],
                            }),
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new TextRun({ text: cap1, font: "\u5B8B\u4F53", size: 16, italics: true, color: "666666" }),
                                ],
                            }),
                        ],
                    }),
                    new TableCell({
                        borders,
                        width: { size: 3009, type: WidthType.DXA },
                        verticalAlign: "center",
                        children: [
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new ImageRun({
                                        type: "png",
                                        data: readImage(img2),
                                        transformation: { width: 200, height: 140 },
                                        altText: { title: cap2, description: cap2, name: img2 },
                                    }),
                                ],
                            }),
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new TextRun({ text: cap2, font: "\u5B8B\u4F53", size: 16, italics: true, color: "666666" }),
                                ],
                            }),
                        ],
                    }),
                    new TableCell({
                        borders,
                        width: { size: 3009, type: WidthType.DXA },
                        verticalAlign: "center",
                        children: [
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new ImageRun({
                                        type: "png",
                                        data: readImage(img3),
                                        transformation: { width: 200, height: 140 },
                                        altText: { title: cap3, description: cap3, name: img3 },
                                    }),
                                ],
                            }),
                            new Paragraph({
                                alignment: AlignmentType.CENTER,
                                children: [
                                    new TextRun({ text: cap3, font: "\u5B8B\u4F53", size: 16, italics: true, color: "666666" }),
                                ],
                            }),
                        ],
                    }),
                ],
            }),
        ],
    });
}

// ============================================================
// Content using Unicode escapes to avoid quote conflicts
// ============================================================
const LQ = '\u201C'; // left double quote
const RQ = '\u201D'; // right double quote
const LSL = '\u300A'; // left angle bracket
const RSL = '\u300B'; // right angle bracket
const DASH = '\u2014'; // em dash

// ============================================================
// Build the document content
// ============================================================
const children = [];

// ===================== COVER PAGE =====================
children.push(new Paragraph({ spacing: { before: 400 }, alignment: AlignmentType.CENTER, children: [] }));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, children: [] }));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, children: [] }));
children.push(new Paragraph({
    alignment: AlignmentType.CENTER,
    children: [new TextRun({ text: '\u5E7F\u897F\u79D1\u6280\u5927\u5B66', font: '\u9ED1\u4F53', size: 52, bold: true })],
}));
children.push(new Paragraph({
    alignment: AlignmentType.CENTER,
    children: [new TextRun({ text: '\u5B9E\u8BAD\u9879\u76EE\u62A5\u544A\u4E66', font: '\u9ED1\u4F53', size: 52, bold: true })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, children: [] }));
children.push(new Paragraph({
    alignment: AlignmentType.CENTER,
    spacing: { after: 100 },
    children: [
        new TextRun({ text: '\u8BFE\u9898\u540D\u79F0  ', font: '\u5B8B\u4F53', size: 28 }),
        new TextRun({ text: LSL + '\u57FA\u4E8EDeepSeek+Trae+Devbox Tlias\u9879\u76EE\u5F00\u53D1\u4E0E\u90E8\u7F72' + RSL, font: '\u5B8B\u4F53', size: 28, underline: { type: 'single' } }),
    ],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 },
    children: [new TextRun({ text: '\u7EC4\u957F\uFF1A\u9ECE\u6D77\u658C  202300406028', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 },
    children: [new TextRun({ text: '\u7EC4\u5458\uFF1A\u9646\u56FD\u7075  202300406020  \u90D9\u6893\u4E50  202300406018', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 },
    children: [new TextRun({ text: '\u5468\u660E\u4EAE  202300406021  \u4F55\u8FC8    202300406022', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 150 },
    children: [new TextRun({ text: '\u674E\u4FCA\u6F6E  202300406025  \u8BB8\u6770    202300406024', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 },
    children: [new TextRun({ text: '\u5B66    \u9662        _\u8BA1\u7B97\u673A\u79D1\u5B66\u4E0E\u6280\u672F\u5B66\u9662______', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 },
    children: [new TextRun({ text: '\u4E13    \u4E1A  __________\u8F6F\u4EF6\u5DE5\u7A0B_______________', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 },
    children: [new TextRun({ text: '\u73ED    \u7EA7            \u8F6F\u4EF6231  ______________', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 80 },
    children: [new TextRun({ text: '\u6307\u5BFC\u6559\u5E08            \u674E\u946B\u3001\u6768\u5A77_____________ ', font: '\u5B8B\u4F53', size: 28 })],
}));
children.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 200 },
    children: [new TextRun({ text: '2026\u5E74  7\u6708  2\u65E5', font: '\u5B8B\u4F53', size: 28 })],
}));

children.push(new Paragraph({ children: [new PageBreak()] }));

// ===================== 1. Project Source & Background =====================
children.push(sectionTitle('\u4E00\u3001\u9879\u76EE\u6765\u6E90\u53CA\u80CC\u666F'));
children.push(bodyText('\u968F\u7740\u6211\u56FD\u533B\u7597\u536B\u751F\u4FE1\u606F\u5316\u7684\u5FEB\u901F\u53D1\u5C55\u548C' + LQ + '\u5065\u5EB7\u4E2D\u56FD' + RQ + '\u6218\u7565\u7684\u6DF1\u5165\u63A8\u8FDB\uFF0C\u4EBA\u4EEC\u5BF9\u5065\u5EB7\u7BA1\u7406\u7684\u9700\u6C42\u65E5\u76CA\u589E\u957F\u3002\u4F20\u7EDF\u7684\u533B\u7597\u5065\u5EB7\u7BA1\u7406\u6A21\u5F0F\u4E3B\u8981\u4F9D\u8D56\u7EBF\u4E0B\u5C31\u8BCA\u548C\u7EB8\u8D28\u6863\u6848\uFF0C\u5B58\u5728\u4FE1\u606F\u5206\u6563\u3001\u67E5\u8BE2\u4E0D\u4FBF\u3001\u6548\u7387\u4F4E\u4E0B\u7B49\u95EE\u9898\u3002\u7279\u522B\u662F\u5728\u57FA\u5C42\u533B\u7597\u673A\u6784\u4E2D\uFF0C\u5065\u5EB7\u7BA1\u7406\u7684\u6570\u5B57\u5316\u3001\u667A\u80FD\u5316\u6C34\u5E73\u4ECD\u6709\u8F83\u5927\u63D0\u5347\u7A7A\u95F4\u3002'));
children.push(bodyText('\u5728\u6B64\u80CC\u666F\u4E0B\uFF0C\u672C\u9879\u76EE' + LQ + '\u533B\u7597\u7BA1\u5BB6\u5065\u5EB7\u7BA1\u7406\u7CFB\u7EDF' + RQ + '\u5E94\u8FD0\u800C\u751F\u3002\u8BE5\u9879\u76EE\u65E8\u5728\u5229\u7528\u73B0\u4EE3\u4FE1\u606F\u6280\u672F\uFF0C\u6784\u5EFA\u4E00\u4E2A\u96C6\u4F1A\u5458\u7BA1\u7406\u3001\u4F53\u68C0\u5957\u9910\u7BA1\u7406\u3001\u5065\u5EB7\u6863\u6848\u7BA1\u7406\u3001\u9884\u7EA6\u6302\u53F7\u3001AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\u3001\u6570\u636E\u7EDF\u8BA1\u5206\u6790\u7B49\u529F\u80FD\u4E8E\u4E00\u4F53\u7684\u7EFC\u5408\u6027\u5065\u5EB7\u7BA1\u7406\u5E73\u53F0\u3002\u7CFB\u7EDF\u9762\u5411\u533B\u7597\u673A\u6784\u7BA1\u7406\u8005\u548C\u666E\u901A\u7528\u6237\u4E24\u4E2A\u7FA4\u4F53\uFF0C\u63D0\u4F9BPC\u7F51\u9875\u7AEF\u7BA1\u7406\u540E\u53F0\u548C\u79FB\u52A8\u7AEF\u7528\u6237\u5E94\u7528\u4E24\u79CD\u8BBF\u95EE\u65B9\u5F0F\uFF0C\u5B9E\u73B0\u5168\u6D41\u7A0B\u8986\u76D6\u7684\u5065\u5EB7\u7BA1\u7406\u670D\u52A1\u3002'));
children.push(bodyText('\u672C\u9879\u76EE\u7684\u6280\u672F\u5B9E\u73B0\u6DF1\u5EA6\u7ED3\u5408\u4E8EAI\u7F16\u7A0B\u5DE5\u5177\uFF08DeepSeek+Trae+Devbox\uFF09\uFF0C\u901A\u8FC7AI\u8F85\u52A9\u5F00\u53D1\u63D0\u5347\u4E86\u5F00\u53D1\u6548\u7387\uFF0C\u63A2\u7D22\u4E86\u65B0\u4E00\u4EE3\u667A\u80FD\u5F00\u53D1\u5DE5\u5177\u5728\u5B9E\u9645\u9879\u76EE\u4E2D\u7684\u5E94\u7528\u65B9\u5F0F\uFF0C\u5177\u6709\u8F83\u5F3A\u7684\u5B9E\u8DF5\u610F\u4E49\u548C\u521B\u65B0\u4EF7\u503C\u3002\u9879\u76EE\u6DB5\u76D6\u540E\u7AEFSpring Boot\u670D\u52A1\u5F00\u53D1\u3001MySQL\u6570\u636E\u5E93\u8BBE\u8BA1\u3001\u79FB\u52A8\u7AEFH5\u9875\u9762\u5F00\u53D1\u7B49\u591A\u4E2A\u6280\u672F\u9886\u57DF\uFF0C\u662F\u4E00\u4E2A\u7EFC\u5408\u6027\u8F83\u5F3A\u7684\u8F6F\u4EF6\u5DE5\u7A0B\u5B9E\u8DF5\u9879\u76EE\u3002'));

// ===================== 2. Research Status =====================
children.push(sectionTitle('\u4E8C\u3001\u56FD\u5185\u5916\u7814\u7A76\u5DE5\u7A0B\u73B0\u72B6'));
children.push(subTitle('2.1 \u56FD\u5916\u7814\u7A76\u73B0\u72B6'));
children.push(bodyText('\u5728\u56FD\u9645\u8303\u56F4\u5185\uFF0C\u533B\u7597\u5065\u5EB7\u7BA1\u7406\u4FE1\u606F\u5316\u8D77\u6B65\u8F83\u65E9\uFF0C\u7F8E\u56FD\u7684Epic Systems\u548CCerner\u7B49\u7535\u5B50\u5065\u5EB7\u8BB0\u5F55\uFF08EHR\uFF09\u7CFB\u7EDF\u5DF2\u7ECF\u5728\u5927\u578B\u533B\u7597\u673A\u6784\u4E2D\u5E7F\u6CDB\u5E94\u7528\u3002\u8C37\u6B4C\u5065\u5EB7\uFF08Google Health\uFF09\u3001\u82F9\u679C\u5065\u5EB7\uFF08Apple Health\uFF09\u7B49\u79D1\u6280\u5DE8\u5934\u4E5F\u7EB7\u7EB7\u5E03\u5C40\u4E2A\u4EBA\u5065\u5EB7\u7BA1\u7406\u9886\u57DF\uFF0C\u63A8\u51FA\u53EF\u7A7F\u6234\u8BBE\u5907\u4E0E\u624B\u673A\u5E94\u7528\u8054\u52A8\u7684\u5065\u5EB7\u76D1\u6D4B\u65B9\u6848\u3002\u6B64\u5916\uFF0CAI\u8F85\u52A9\u8BCA\u65AD\u548C\u667A\u80FD\u5065\u5EB7\u54A8\u8BE2\u7CFB\u7EDF\uFF08\u5982IBM Watson Health\uFF09\u4E5F\u5728\u4E34\u5E8A\u51B3\u7B56\u652F\u6301\u65B9\u9762\u53D6\u5F97\u4E86\u663E\u8457\u8FDB\u5C55\u3002\u8FD1\u5E74\u6765\uFF0C\u8FDC\u7A0B\u533B\u7597\u548C\u865A\u62DF\u8BCA\u6240\u7684\u6982\u5FF5\u5728\u5168\u7403\u8303\u56F4\u5185\u8FC5\u901F\u666E\u53CA\uFF0C\u65B0\u51A0\u75AB\u60C5\u66F4\u662F\u52A0\u901F\u4E86\u8FD9\u4E00\u8D8B\u52BF\u7684\u53D1\u5C55\u3002'));
children.push(subTitle('2.2 \u56FD\u5185\u7814\u7A76\u73B0\u72B6'));
children.push(bodyText('\u56FD\u5185\u533B\u7597\u4FE1\u606F\u5316\u5EFA\u8BBE\u8FD1\u5E74\u6765\u53D6\u5F97\u4E86\u957F\u8DB3\u8FDB\u6B65\u3002\u963F\u91CC\u5065\u5EB7\u3001\u5E73\u5B89\u597D\u533B\u751F\u3001\u5FAE\u533B\u7B49\u5E73\u53F0\u5DF2\u7ECF\u5EFA\u7ACB\u4E86\u8F83\u4E3A\u5B8C\u5584\u7684\u5728\u7EBF\u5065\u5EB7\u670D\u52A1\u4F53\u7CFB\u3002\u56FD\u52A1\u9662\u53D1\u5E03\u7684' + LSL + LQ + '\u5065\u5EB7\u4E2D\u56FD2030' + RQ + '\u89C4\u5212\u7EB2\u8981' + RSL + '\u660E\u786E\u63D0\u51FA\u8981\u63A8\u8FDB\u5065\u5EB7\u533B\u7597\u5927\u6570\u636E\u5E94\u7528\uFF0C\u53D1\u5C55\u667A\u6167\u5065\u5EB7\u533B\u7597\u3002\u5404\u7EA7\u533B\u7597\u673A\u6784\u7EB7\u7EB7\u5EFA\u8BBE\u81EA\u5DF1\u7684\u4FE1\u606F\u5316\u7BA1\u7406\u7CFB\u7EDF\uFF0C\u4F46\u57FA\u5C42\u533B\u7597\u673A\u6784\u5728\u5065\u5EB7\u7BA1\u7406\u6570\u5B57\u5316\u65B9\u9762\u4ECD\u5B58\u5728\u77ED\u677F\u3002\u8BB8\u591A\u4E2D\u5C0F\u578B\u8BCA\u6240\u548C\u4F53\u68C0\u4E2D\u5FC3\u4F7F\u7528\u7684\u7BA1\u7406\u7CFB\u7EDF\u529F\u80FD\u5355\u4E00\u3001\u7528\u6237\u4F53\u9A8C\u5DEE\u3001\u6570\u636E\u4E92\u901A\u56F0\u96BE\uFF0C\u4E9E\u9700\u4E00\u5957\u529F\u80FD\u5B8C\u5584\u3001\u64CD\u4F5C\u4FBF\u6377\u3001\u652F\u6301\u591A\u7AEF\u8BBF\u95EE\u7684\u7EFC\u5408\u5065\u5EB7\u7BA1\u7406\u89E3\u51B3\u65B9\u6848\u3002'));
children.push(bodyText('\u672C\u9879\u76EE\u6B63\u662F\u5728\u8FD9\u4E00\u884C\u4E1A\u80CC\u666F\u4E0B\u5F00\u53D1\u7684\uFF0C\u5B83\u9488\u5BF9\u57FA\u5C42\u533B\u7597\u673A\u6784\u5065\u5EB7\u7BA1\u7406\u7684\u5B9E\u9645\u9700\u6C42\uFF0C\u63D0\u4F9B\u4E86\u4E00\u5957\u6DB5\u76D6\u4F1A\u5458\u7BA1\u7406\u3001\u5957\u9910\u7BA1\u7406\u3001\u9884\u7EA6\u7BA1\u7406\u3001\u5065\u5EB7\u6863\u6848\u3001AI\u667A\u80FD\u52A9\u624B\u7B49\u529F\u80FD\u7684\u5B8C\u6574\u89E3\u51B3\u65B9\u6848\uFF0C\u586B\u8865\u4E86\u4E2D\u5C0F\u578B\u533B\u7597\u673A\u6784\u7EFC\u5408\u5065\u5EB7\u7BA1\u7406\u7CFB\u7EDF\u7684\u5E02\u573A\u7A7A\u767D\u3002'));

// ===================== 3. Technical Route =====================
children.push(sectionTitle('\u4E09\u3001\u6280\u672F\u8DEF\u7EBF' + DASH + DASH + '\u6309\u4F55\u79CD\u65B9\u5F0F\u89E3\u51B3\u4F55\u79CD\u6280\u672F\u96BE\u9898'));
children.push(subTitle('3.1 \u603B\u4F53\u6280\u672F\u67B6\u6784'));
children.push(bodyText('\u672C\u9879\u76EE\u91C7\u7528\u524D\u540E\u7AEF\u5206\u79BB\u7684B/S\uFF08Browser/Server\uFF09\u67B6\u6784\uFF0C\u524D\u7AEF\u901A\u8FC7\u6D4F\u89C8\u5668\u8BBF\u95EE\uFF0C\u540E\u7AEF\u63D0\u4F9BRESTful API\u63A5\u53E3\u3002\u6574\u4F53\u6280\u672F\u6808\u5305\u62EC\uFF1A\u540E\u7AEF\u57FA\u4E8ESpring Boot\u6846\u67B6\uFF0C\u96C6\u6210MyBatis Plus\u4F5C\u4E3AORM\u6846\u67B6\uFF0C\u4F7F\u7528MySQL\u4F5C\u4E3A\u5173\u7CFB\u578B\u6570\u636E\u5E93\uFF0CRedis\u4F5C\u4E3A\u7F13\u5B58\u4E2D\u95F4\u4EF6\uFF1BPC\u7BA1\u7406\u7AEF\u524D\u7AEF\u4F7F\u7528Vue.js + Element Plus\u6784\u5EFA\u5355\u9875\u5E94\u7528\uFF1B\u79FB\u52A8\u7AEF\u91C7\u7528\u539F\u751FHTML5 + CSS3 + JavaScript\u5F00\u53D1\uFF0C\u901A\u8FC7\u54CD\u5E94\u5F0F\u5E03\u5C40\u9002\u914D\u4E0D\u540C\u5C4F\u5E55\u5C3A\u5BF8\u7684\u79FB\u52A8\u8BBE\u5907\u3002'));
children.push(subTitle('3.2 \u5173\u952E\u6280\u672F\u96BE\u9898\u53CA\u89E3\u51B3\u65B9\u6848'));
children.push(bodyText('\uFF081\uFF09AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\u7684\u96C6\u6210\uFF1A\u7CFB\u7EDF\u96C6\u6210\u4E86\u57FA\u4E8EDeepSeek\u5927\u6A21\u578B\u7684AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\uFF0C\u7528\u6237\u53EF\u4EE5\u901A\u8FC7\u81EA\u7136\u8BED\u8A00\u4E0EAI\u8FDB\u884C\u5065\u5EB7\u54A8\u8BE2\u5BF9\u8BDD\u3002\u6280\u672F\u5B9E\u73B0\u4E0A\uFF0C\u901A\u8FC7\u540E\u7AEAiController\u8C03\u7528DeepSeek API\uFF0C\u5B9E\u73B0\u6D41\u5F0F\u54CD\u5E94\uFF08SSE\uFF09\uFF0C\u524D\u7AEF\u4F7F\u7528EventSource\u63A5\u6536\u5B9E\u65F6\u8FD4\u56DE\u7684AI\u56DE\u7B54\uFF0C\u63D0\u4F9B\u4E86\u7C7B\u4F3CChatGPT\u7684\u4EA4\u4E92\u4F53\u9A8C\u3002'));
children.push(bodyText('\uFF082\uFF09\u591A\u7AEF\u6570\u636E\u540C\u6B65\uFF1APC\u7BA1\u7406\u7AEF\u548C\u79FB\u52A8\u7AEF\u5171\u7528\u540C\u4E00\u5957\u540E\u7AEF\u670D\u52A1\u548C\u6570\u636E\u5E93\uFF0C\u901A\u8FC7\u7EDF\u4E00\u7684RESTful API\u63A5\u53E3\u5B9E\u73B0\u6570\u636E\u540C\u6B65\u3002\u4F8B\u5982\uFF0C\u7BA1\u7406\u5458\u5728PC\u7AEF\u4FEE\u6539\u7684\u5957\u9910\u4FE1\u606F\u4F1A\u7ACB\u5373\u5728\u79FB\u52A8\u7AEF\u5C55\u793A\uFF1B\u7528\u6237\u5728\u79FB\u52A8\u7AEF\u63D0\u4EA4\u7684\u9884\u7EA6\u8BA2\u5355\u53EF\u4EE5\u5728PC\u7BA1\u7406\u7AEF\u5B9E\u65F6\u67E5\u770B\u548C\u5904\u7406\u3002'));
children.push(bodyText('\uFF083\uFF09\u590D\u6742\u4E1A\u52A1\u903B\u8F91\u5904\u7406\uFF1A\u4F53\u68C0\u5957\u9910\u3001\u68C0\u67E5\u7EC4\u3001\u68C0\u67E5\u9879\u4E4B\u95F4\u5B58\u5728\u591A\u5BF9\u591A\u7684\u5173\u8054\u5173\u7CFB\uFF0C\u7CFB\u7EDF\u901A\u8FC7\u4E2D\u95F4\u8868\uFF08SetmealCheckgroup\u3001CheckgroupCheckitem\uFF09\u5B9E\u73B0\u7075\u6D3B\u7684\u5173\u8054\u7BA1\u7406\uFF0C\u652F\u6301\u52A8\u6001\u914D\u7F6E\u4F53\u68C0\u9879\u76EE\u7684\u7EC4\u5408\u65B9\u5F0F\u3002'));
children.push(bodyText('\uFF084\uFF09\u89D2\u8272\u6743\u9650\u63A7\u5236\uFF1A\u7CFB\u7EDF\u5B9E\u73B0\u4E86\u57FA\u4E8ERBAC\uFF08Role-Based Access Control\uFF09\u7684\u6743\u9650\u7BA1\u7406\u6A21\u578B\uFF0C\u901A\u8FC7User\u3001Role\u3001Permission\u4E09\u5F20\u6838\u5FC3\u8868\u53CA\u5173\u8054\u8868\uFF0C\u5B9E\u73B0\u4E86\u7EC6\u7C92\u5EA6\u7684\u529F\u80FD\u6743\u9650\u63A7\u5236\uFF0C\u786E\u4FDD\u4E0D\u540C\u89D2\u8272\u7684\u7528\u6237\u53EA\u80FD\u8BBF\u95EE\u5176\u6388\u6743\u8303\u56F4\u5185\u7684\u529F\u80FD\u6A21\u5757\u3002'));
children.push(bodyText('\uFF085\uFF09\u6587\u4EF6\u4E0A\u4F20\u4E0E\u5065\u5EB7\u6570\u636E\u7BA1\u7406\uFF1A\u7CFB\u7EDF\u5B9E\u73B0\u4E86\u5065\u5EB7\u6570\u636E\u4E0A\u4F20\u529F\u80FD\uFF08FileUploadController\uFF09\uFF0C\u652F\u6301\u7528\u6237\u4E0A\u4F20\u5065\u5EB7\u68C0\u67E5\u62A5\u544A\u7B49\u6587\u4EF6\uFF0C\u540E\u7AEF\u5BF9\u6587\u4EF6\u8FDB\u884C\u5B58\u50A8\u548C\u7BA1\u7406\uFF0C\u5E76\u4E0E\u7528\u6237\u7684\u5065\u5EB7\u6863\u6848\u5173\u8054\u3002'));

// ===================== 4. Innovation Points =====================
children.push(sectionTitle('\u56DB\u3001\u9879\u76EE\u521B\u65B0\u70B9'));
children.push(bodyText('\u672C\u9879\u76EE\u5728\u4EE5\u4E0B\u65B9\u9762\u5177\u6709\u663E\u8457\u521B\u65B0\u70B9\uFF1A'));
children.push(bodyText('\uFF081\uFF09AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\uFF1A\u7CFB\u7EDF\u96C6\u6210\u4E86\u57FA\u4E8EDeepSeek\u5927\u6A21\u578B\u7684AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\uFF0C\u80FD\u591F\u6839\u636E\u7528\u6237\u63CF\u8FF0\u7684\u75C7\u72B6\u548C\u5065\u5EB7\u72B6\u51B5\uFF0C\u63D0\u4F9B\u521D\u6B65\u7684\u5065\u5EB7\u54A8\u8BE2\u5EFA\u8BAE\u548C\u4F53\u68C0\u65B9\u6848\u63A8\u8350\u3002\u8FD9\u4E00\u529F\u80FD\u7A81\u7834\u4E86\u4F20\u7EDF\u5065\u5EB7\u7BA1\u7406\u7CFB\u7EDF\u4EC5\u63D0\u4F9B\u4FE1\u606F\u5C55\u793A\u7684\u5C40\u9650\uFF0C\u5B9E\u73B0\u4E86\u667A\u80FD\u5316\u7684\u5065\u5EB7\u54A8\u8BE2\u670D\u52A1\uFF0C\u63D0\u5347\u4E86\u7528\u6237\u4F53\u9A8C\u3002AI\u52A9\u624B\u652F\u6301\u6D41\u5F0F\u54CD\u5E94\uFF0C\u56DE\u7B54\u5B9E\u65F6\u663E\u793A\uFF0C\u4EA4\u4E92\u4F53\u9A8C\u6D41\u7545\u81EA\u7136\u3002'));
children.push(bodyText('\uFF082\uFF09PC\u7F51\u9875\u7AEF\u4E0E\u79FB\u52A8\u7AEF\u534F\u540C\uFF1A\u7CFB\u7EDF\u540C\u65F6\u63D0\u4F9BPC\u7BA1\u7406\u7AEF\u548C\u79FB\u52A8\u7AEF\u7528\u6237\u5E94\u7528\uFF0C\u4E24\u8005\u6570\u636E\u5B9E\u65F6\u540C\u6B65\u3002PC\u7AEF\u9762\u5411\u533B\u7597\u673A\u6784\u7BA1\u7406\u5458\uFF0C\u63D0\u4F9B\u4F1A\u5458\u7BA1\u7406\u3001\u5957\u9910\u7BA1\u7406\u3001\u68C0\u67E5\u7EC4\u7BA1\u7406\u3001\u68C0\u67E5\u9879\u7BA1\u7406\u3001\u8BA2\u5355\u7BA1\u7406\u3001\u6570\u636E\u7EDF\u8BA1\u62A5\u8868\u7B49\u5B8C\u6574\u7684\u540E\u53F0\u7BA1\u7406\u529F\u80FD\uFF1B\u79FB\u52A8\u7AEF\u9762\u5411\u666E\u901A\u7528\u6237\uFF0C\u63D0\u4F9B\u767B\u5F55\u6CE8\u518C\u3001\u9996\u9875\u3001\u4F53\u68C0\u5957\u9910\u67E5\u770B\u4E0E\u9884\u7EA6\u3001\u5065\u5EB7\u6863\u6848\u7BA1\u7406\u3001\u5065\u5EB7\u98CE\u9669\u8BC4\u4F30\u3001\u5065\u5EB7\u4FE1\u606F\u67E5\u8BE2\u3001\u9884\u7EA6\u4FE1\u606F\u7BA1\u7406\u7B49\u529F\u80FD\u3002\u591A\u7AEF\u534F\u540C\u7684\u8BBE\u8BA1\u6A21\u5F0F\u6EE1\u8DB3\u4E86\u4E0D\u540C\u7528\u6237\u7FA4\u4F53\u7684\u4F7F\u7528\u9700\u6C42\u3002'));
children.push(bodyText('\uFF083\uFF09\u79FB\u52A8\u7AEF\u4E2A\u4EBA\u4E2D\u5FC3\u4E0E\u4FE1\u606F\u7BA1\u7406\uFF1A\u79FB\u52A8\u7AEF\u63D0\u4F9B\u4E86\u5B8C\u5584\u7684\u4E2A\u4EBA\u4E2D\u5FC3\u529F\u80FD\uFF0C\u7528\u6237\u53EF\u4EE5\u7BA1\u7406\u4E2A\u4EBA\u4FE1\u606F\u3001\u67E5\u770B\u548C\u4FEE\u6539\u9884\u7EA6\u4FE1\u606F\u3001\u67E5\u8BE2\u5065\u5EB7\u6863\u6848\u3001\u8FDB\u884C\u5065\u5EB7\u98CE\u9669\u8BC4\u4F30\u7B49\u3002\u7CFB\u7EDF\u8FD8\u63D0\u4F9B\u4E86\u5065\u5EB7\u4FE1\u606F\u63A8\u9001\u548C\u9884\u7EA6\u901A\u77E5\u529F\u80FD\uFF0C\u5E2E\u52A9\u7528\u6237\u53CA\u65F6\u4E86\u89E3\u81EA\u8EAB\u5065\u5EB7\u72B6\u51B5\u548C\u5C31\u8BCA\u5B89\u6392\u3002'));
children.push(bodyText('\uFF084\uFF09AI\u8F85\u52A9\u5F00\u53D1\u5B9E\u8DF5\uFF1A\u9879\u76EE\u5168\u7A0B\u91C7\u7528DeepSeek+Trae+Devbox\u7B49AI\u7F16\u7A0B\u5DE5\u5177\u8F85\u52A9\u5F00\u53D1\uFF0C\u63A2\u7D22\u4E86AI\u8F85\u52A9\u7F16\u7A0B\u5728\u5B9E\u9645\u8F6F\u4EF6\u9879\u76EE\u4E2D\u7684\u5E94\u7528\u6A21\u5F0F\u3002\u901A\u8FC7AI\u4EE3\u7801\u751F\u6210\u3001\u4EE3\u7801\u5BA1\u67E5\u3001Bug\u4FEE\u590D\u7B49\u529F\u80FD\uFF0C\u663E\u8457\u63D0\u5347\u4E86\u5F00\u53D1\u6548\u7387\uFF0C\u51CF\u5C11\u4E86\u91CD\u590D\u6027\u7F16\u7801\u5DE5\u4F5C\uFF0C\u4E3AAI\u8F85\u52A9\u8F6F\u4EF6\u5F00\u53D1\u63D0\u4F9B\u4E86\u5B9D\u8D35\u7684\u5B9E\u8DF5\u7ECF\u9A8C\u3002'));
children.push(bodyText('\uFF085\uFF09\u6570\u636E\u9A71\u52A8\u7684\u51B3\u7B56\u652F\u6301\uFF1A\u7CFB\u7EDF\u63D0\u4F9B\u4E86\u4E1A\u52A1\u7EDF\u8BA1\u62A5\u8868\u3001\u4F1A\u5458\u7EDF\u8BA1\u62A5\u8868\u548C\u5957\u9910\u7EDF\u8BA1\u62A5\u8868\u4E09\u4E2A\u7EF4\u5EA6\u7684\u6570\u636E\u5206\u6790\u529F\u80FD\uFF0C\u901A\u8FC7ECharts\u56FE\u8868\u76F4\u89C2\u5C55\u793A\u7ECF\u8425\u6570\u636E\uFF0C\u5E2E\u52A9\u7BA1\u7406\u8005\u505A\u51FA\u6570\u636E\u9A71\u52A8\u7684\u7ECF\u8425\u51B3\u7B56\u3002'));

// ===================== 5. Problems & Solutions =====================
children.push(sectionTitle('\u4E94\u3001\u9879\u76EE\u8FDB\u884C\u4E2D\u9047\u5230\u7684\u95EE\u9898\u53CA\u89E3\u51B3\u65B9\u6848'));
children.push(subTitle('5.1 \u79FB\u52A8\u7AEF\u9875\u9762\u9002\u914D\u95EE\u9898'));
children.push(bodyText('\u95EE\u9898\u63CF\u8FF0\uFF1A\u79FB\u52A8\u7AEFH5\u9875\u9762\u5728\u4E0D\u540C\u5C3A\u5BF8\u7684\u624B\u673A\u5C4F\u5E55\u4E0A\u663E\u793A\u6548\u679C\u4E0D\u4E00\u81F4\uFF0C\u90E8\u5206\u9875\u9762\u5143\u7D20\u51FA\u73B0\u9519\u4F4D\u6216\u6EA2\u51FA\u3002\u89E3\u51B3\u65B9\u6848\uFF1A\u91C7\u7528CSS\u5A92\u4F53\u67E5\u8BE2\u548C\u5F39\u6027\u5E03\u5C40\uFF08Flexbox\uFF09\u76F8\u7ED3\u5408\u7684\u65B9\u5F0F\uFF0C\u6839\u636E\u4E0D\u540C\u5C4F\u5E55\u5C3A\u5BF8\u52A8\u6001\u8C03\u6574\u9875\u9762\u5143\u7D20\u7684\u5927\u5C0F\u548C\u4F4D\u7F6E\u3002\u540C\u65F6\u4F7F\u7528rem\u5355\u4F4D\u66FF\u4EE3\u56FA\u5B9A\u7684px\u5355\u4F4D\uFF0C\u5B9E\u73B0\u4E86\u8F83\u597D\u7684\u591A\u7AEF\u9002\u914D\u6548\u679C\u3002'));
children.push(subTitle('5.2 AI\u667A\u80FD\u52A9\u624B\u6D41\u5F0F\u54CD\u5E94\u7684\u524D\u7AEF\u6E32\u67D3'));
children.push(bodyText('\u95EE\u9898\u63CF\u8FF0\uFF1ADeepSeek API\u8FD4\u56DE\u7684\u662F\u6D41\u5F0F\u6570\u636E\uFF08SSE\uFF09\uFF0C\u524D\u7AEF\u9700\u8981\u9010\u5B57\u663E\u793AAI\u7684\u56DE\u7B54\u5185\u5BB9\uFF0C\u540C\u65F6\u8FD8\u8981\u5904\u7406Markdown\u683C\u5F0F\u7684\u6E32\u67D3\u3002\u89E3\u51B3\u65B9\u6848\uFF1A\u524D\u7AEF\u4F7F\u7528EventSource API\u76D1\u542CSSE\u4E8B\u4EF6\uFF0C\u5728onmessage\u56DE\u8C03\u4E2D\u9010\u6B65\u62FC\u63A5AI\u8FD4\u56DE\u7684\u6587\u672C\u7247\u6BB5\uFF0C\u5E76\u4F7F\u7528marked.js\u5E93\u5C06Markdown\u683C\u5F0F\u5B9E\u65F6\u8F6C\u6362\u4E3AHTML\uFF0C\u5B9E\u73B0\u4E86\u6D41\u7545\u7684\u6253\u5B57\u673A\u6548\u679C\u548C\u5BCC\u6587\u672C\u6E32\u67D3\u3002'));
children.push(subTitle('5.3 \u591A\u5BF9\u591A\u5173\u8054\u6570\u636E\u7684\u7BA1\u7406'));
children.push(bodyText('\u95EE\u9898\u63CF\u8FF0\uFF1A\u4F53\u68C0\u5957\u9910\u4E0E\u68C0\u67E5\u7EC4\u3001\u68C0\u67E5\u7EC4\u4E0E\u68C0\u67E5\u9879\u4E4B\u95F4\u5B58\u5728\u590D\u6742\u7684\u591A\u5BF9\u591A\u5173\u8054\u5173\u7CFB\uFF0C\u5728\u7F16\u8F91\u5957\u9910\u65F6\u9700\u8981\u540C\u65F6\u7BA1\u7406\u591A\u5C42\u5173\u8054\u5173\u7CFB\u3002\u89E3\u51B3\u65B9\u6848\uFF1A\u91C7\u7528\u4E2D\u95F4\u8868\u8BBE\u8BA1\u6A21\u5F0F\uFF0C\u901A\u8FC7SetmealCheckgroup\u548CCheckgroupCheckitem\u4E2D\u95F4\u8868\u7BA1\u7406\u5173\u8054\u5173\u7CFB\uFF0C\u5728\u7F16\u8F91\u64CD\u4F5C\u65F6\u5148\u5220\u9664\u65E7\u5173\u8054\u518D\u6279\u91CF\u63D2\u5165\u65B0\u5173\u8054\uFF0C\u4F7F\u7528\u4E8B\u52A1\u4FDD\u8BC1\u6570\u636E\u4E00\u81F4\u6027\u3002'));
children.push(subTitle('5.4 \u6587\u4EF6\u4E0A\u4F20\u7684\u5B89\u5168\u6027\u95EE\u9898'));
children.push(bodyText('\u95EE\u9898\u63CF\u8FF0\uFF1A\u5065\u5EB7\u6570\u636E\u4E0A\u4F20\u529F\u80FD\u9700\u8981\u5904\u7406\u7528\u6237\u4E0A\u4F20\u7684\u6587\u4EF6\uFF0C\u5B58\u5728\u6587\u4EF6\u7C7B\u578B\u6821\u9A8C\u548C\u5B58\u50A8\u5B89\u5168\u7B49\u95EE\u9898\u3002\u89E3\u51B3\u65B9\u6848\uFF1A\u5728FileUploadController\u4E2D\u5B9E\u73B0\u4E86\u4E25\u683C\u7684\u6587\u4EF6\u7C7B\u578B\u767D\u540D\u5355\u6821\u9A8C\uFF0C\u9650\u5236\u4E0A\u4F20\u6587\u4EF6\u7684\u5927\u5C0F\u548C\u683C\u5F0F\uFF0C\u6587\u4EF6\u5B58\u50A8\u5230\u6307\u5B9A\u7684uploads\u76EE\u5F55\u4E0B\uFF0C\u5E76\u4EE5\u7528\u6237ID\u548C\u65F6\u95F4\u6233\u547D\u540D\u907F\u514D\u6587\u4EF6\u51B2\u7A81\u3002'));
children.push(subTitle('5.5 AI\u8F85\u52A9\u5F00\u53D1\u4E2D\u7684\u4EE3\u7801\u8D28\u91CF\u95EE\u9898'));
children.push(bodyText('\u95EE\u9898\u63CF\u8FF0\uFF1A\u4F7F\u7528AI\u5DE5\u5177\u751F\u6210\u7684\u4EE3\u7801\u867D\u7136\u6548\u7387\u9AD8\uFF0C\u4F46\u90E8\u5206\u4EE3\u7801\u7684\u547D\u540D\u89C4\u8303\u548C\u6CE8\u91CA\u4E0D\u591F\u7EDF\u4E00\uFF0C\u5B58\u5728\u4E00\u5B9A\u7684\u4EE3\u7801\u98CE\u683C\u5DEE\u5F02\u3002\u89E3\u51B3\u65B9\u6848\uFF1A\u5236\u5B9A\u4E86\u7EDF\u4E00\u7684\u4EE3\u7801\u89C4\u8303\u6587\u6863\uFF0C\u8981\u6C42\u6240\u6709AI\u751F\u6210\u7684\u4EE3\u7801\u90FD\u5FC5\u987B\u7ECF\u8FC7\u4EBA\u5DE5\u5BA1\u67E5\u540E\u518D\u5408\u5E76\u5230\u4E3B\u5206\u652F\u3002\u540C\u65F6\u5229\u7528IDE\u7684\u4EE3\u7801\u683C\u5F0F\u5316\u5DE5\u5177\u548C\u9759\u6001\u4EE3\u7801\u5206\u6790\u5DE5\u5177\uFF0C\u786E\u4FDD\u4EE3\u7801\u8D28\u91CF\u7B26\u5408\u56E2\u961F\u6807\u51C6\u3002'));

// ===================== 6. Deliverables =====================
children.push(sectionTitle('\u516D\u3001\u5B9E\u73B0\u6216\u5B8C\u6210\u7684\u4EA7\u51FA\u7269\u8BF4\u660E'));
children.push(bodyText('\u672C\u9879\u76EE\u6700\u7EC8\u5B8C\u6210\u4E86' + LQ + '\u533B\u7597\u7BA1\u5BB6\u5065\u5EB7\u7BA1\u7406\u7CFB\u7EDF' + RQ + '\u7684\u5168\u90E8\u5F00\u53D1\u5DE5\u4F5C\uFF0C\u5305\u542BPC\u7F51\u9875\u7AEF\u7BA1\u7406\u540E\u53F0\u548C\u79FB\u52A8\u7AEF\u7528\u6237\u5E94\u7528\u4E24\u5927\u90E8\u5206\u3002\u4EE5\u4E0B\u5206\u522B\u5BF9\u5404\u529F\u80FD\u6A21\u5757\u7684\u9875\u9762\u548C\u529F\u80FD\u8FDB\u884C\u8BF4\u660E\uFF0C\u5E76\u9644\u4E0A\u5B9E\u9645\u8FD0\u884C\u622A\u56FE\u3002'));

children.push(subTitle('6.1 PC\u7F51\u9875\u7AEF\u7BA1\u7406\u540E\u53F0'));
children.push(bodyText('PC\u7AEF\u7BA1\u7406\u540E\u53F0\u57FA\u4E8EVue.js + Element Plus\u6784\u5EFA\uFF0C\u91C7\u7528\u5DE6\u4FA7\u5BFC\u822A\u680F + \u53F3\u4FA7\u5185\u5BB9\u533A\u7684\u7ECF\u5178\u5E03\u5C40\uFF0C\u63D0\u4F9B\u7CFB\u7EDF\u767B\u5F55\u3001\u6570\u636E\u4EEA\u8868\u677F\u3001\u4F1A\u5458\u7BA1\u7406\u3001\u5957\u9910\u7BA1\u7406\u3001\u68C0\u67E5\u7EC4\u7BA1\u7406\u3001\u68C0\u67E5\u9879\u7BA1\u7406\u3001\u8BA2\u5355\u7BA1\u7406\u3001AI\u667A\u80FD\u52A9\u624B\u3001\u6570\u636E\u7EDF\u8BA1\u62A5\u8868\u3001\u5065\u5EB7\u6570\u636E\u4E0A\u4F20\u7B49\u529F\u80FD\u6A21\u5757\u3002'));

children.push(bodyText('\uFF081\uFF09\u7CFB\u7EDF\u767B\u5F55\uFF1A\u7BA1\u7406\u5458\u901A\u8FC7\u8F93\u5165\u7528\u6237\u540D\u548C\u5BC6\u7801\u767B\u5F55\u540E\u53F0\u7BA1\u7406\u7CFB\u7EDF\uFF0C\u7CFB\u7EDF\u652F\u6301\u8BB0\u4F4F\u5BC6\u7801\u529F\u80FD\u3002'));
children.push(...imageWithCaption('be_login.png', '\u56FE1 PC\u7AEF\u7BA1\u7406\u540E\u53F0\u767B\u5F55\u9875\u9762', 480, 270));

children.push(bodyText('\uFF082\uFF09\u6570\u636E\u4EEA\u8868\u677F\uFF1A\u767B\u5F55\u540E\u8FDB\u5165\u4EEA\u8868\u677F\u9875\u9762\uFF0C\u5C55\u793A\u4F1A\u5458\u6570\u91CF\u3001\u8BA2\u5355\u6570\u91CF\u3001\u5957\u9910\u6570\u91CF\u7B49\u5173\u952E\u4E1A\u52A1\u6570\u636E\u6307\u6807\uFF0C\u5E76\u63D0\u4F9B\u5FEB\u6377\u64CD\u4F5C\u5165\u53E3\u3002'));
children.push(...imageWithCaption('be_dashboard.png', '\u56FE2 PC\u7AEF\u6570\u636E\u4EEA\u8868\u677F\u9875\u9762', 480, 270));

children.push(bodyText('\uFF083\uFF09\u4F1A\u5458\u7BA1\u7406\uFF1A\u652F\u6301\u67E5\u770B\u3001\u65B0\u589E\u3001\u7F16\u8F91\u548C\u5220\u9664\u4F1A\u5458\u4FE1\u606F\uFF0C\u63D0\u4F9B\u5206\u9875\u67E5\u8BE2\u548C\u6761\u4EF6\u641C\u7D22\u529F\u80FD\u3002'));
children.push(...imageWithCaption('be_member.png', '\u56FE3 \u4F1A\u5458\u7BA1\u7406\u9875\u9762', 480, 270));

children.push(bodyText('\uFF084\uFF09\u5957\u9910\u7BA1\u7406\uFF1A\u7BA1\u7406\u4F53\u68C0\u5957\u9910\u4FE1\u606F\uFF0C\u5305\u62EC\u5957\u9910\u540D\u79F0\u3001\u4EF7\u683C\u3001\u5173\u8054\u68C0\u67E5\u7EC4\u7B49\u914D\u7F6E\u3002'));
children.push(...imageWithCaption('be_setmeal.png', '\u56FE4 \u5957\u9910\u7BA1\u7406\u9875\u9762', 480, 270));

children.push(bodyText('\uFF085\uFF09\u68C0\u67E5\u7EC4\u4E0E\u68C0\u67E5\u9879\u7BA1\u7406\uFF1A\u7BA1\u7406\u4F53\u68C0\u5957\u9910\u4E2D\u7684\u68C0\u67E5\u7EC4\u53CA\u5176\u5305\u542B\u7684\u5177\u4F53\u68C0\u67E5\u9879\u76EE\u3002'));
children.push(twoImagesRow(
    'be_checkgroup.png', '\u56FE5 \u68C0\u67E5\u7EC4\u7BA1\u7406\u9875\u9762',
    'be_checkitem.png', '\u56FE6 \u68C0\u67E5\u9879\u7BA1\u7406\u9875\u9762'
));

children.push(bodyText('\uFF086\uFF09\u8BA2\u5355\u7BA1\u7406\uFF1A\u67E5\u770B\u548C\u5904\u7406\u7528\u6237\u7684\u9884\u7EA6\u8BA2\u5355\uFF0C\u5305\u62EC\u8BA2\u5355\u72B6\u6001\u8DDF\u8E2A\u548C\u7BA1\u7406\u64CD\u4F5C\u3002'));
children.push(...imageWithCaption('be_orderlist.png', '\u56FE7 \u8BA2\u5355\u7BA1\u7406\u9875\u9762', 480, 270));

children.push(bodyText('\uFF087\uFF09\u6570\u636E\u7EDF\u8BA1\u62A5\u8868\uFF1A\u63D0\u4F9B\u4E1A\u52A1\u7EDF\u8BA1\u3001\u4F1A\u5458\u7EDF\u8BA1\u548C\u5957\u9910\u7EDF\u8BA1\u4E09\u4E2A\u7EF4\u5EA6\u7684ECharts\u6570\u636E\u53EF\u89C6\u5316\u62A5\u8868\u3002'));
children.push(threeImagesRow(
    'report_business.png', '\u56FE8 \u4E1A\u52A1\u7EDF\u8BA1\u62A5\u8868',
    'report_member.png', '\u56FE9 \u4F1A\u5458\u7EDF\u8BA1\u62A5\u8868',
    'report_setmeal.png', '\u56FE10 \u5957\u9910\u7EDF\u8BA1\u62A5\u8868'
));

children.push(bodyText('\uFF088\uFF09\u5065\u5EB7\u6570\u636E\u4E0A\u4F20\uFF1A\u7BA1\u7406\u5458\u53EF\u4E0A\u4F20\u548C\u7BA1\u7406\u7528\u6237\u7684\u5065\u5EB7\u68C0\u67E5\u6570\u636E\u6587\u4EF6\u3002'));
children.push(...imageWithCaption('be_healthupload.png', '\u56FE11 \u5065\u5EB7\u6570\u636E\u4E0A\u4F20\u9875\u9762', 480, 270));

children.push(subTitle('6.2 \u79FB\u52A8\u7AEF\u7528\u6237\u5E94\u7528'));
children.push(bodyText('\u79FB\u52A8\u7AEF\u5E94\u7528\u91C7\u7528\u539F\u751FHTML5 + CSS3 + JavaScript\u5F00\u53D1\uFF0C\u901A\u8FC7\u54CD\u5E94\u5F0F\u5E03\u5C40\u9002\u914D\u4E0D\u540C\u5C3A\u5BF8\u7684\u79FB\u52A8\u8BBE\u5907\u3002\u63D0\u4F9B\u767B\u5F55\u6CE8\u518C\u3001\u9996\u9875\u3001\u4F53\u68C0\u5957\u9910\u6D4F\u89C8\u4E0E\u9884\u7EA6\u3001\u5065\u5EB7\u6863\u6848\u7BA1\u7406\u3001\u5065\u5EB7\u98CE\u9669\u8BC4\u4F30\u3001\u5065\u5EB7\u4FE1\u606F\u67E5\u8BE2\u3001AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\u7B49\u529F\u80FD\u3002'));

children.push(bodyText('\uFF081\uFF09\u79FB\u52A8\u7AEF\u767B\u5F55\uFF1A\u7528\u6237\u901A\u8FC7\u624B\u673A\u53F7/\u7528\u6237\u540D\u548C\u5BC6\u7801\u767B\u5F55\u79FB\u52A8\u7AEF\u5E94\u7528\uFF0C\u652F\u6301\u65B0\u7528\u6237\u6CE8\u518C\u3002'));
children.push(...imageWithCaption('mobile_login.png', '\u56FE12 \u79FB\u52A8\u7AEF\u767B\u5F55\u9875\u9762', 240, 430));

children.push(bodyText('\uFF082\uFF09\u79FB\u52A8\u7AEF\u9996\u9875\uFF1A\u5C55\u793A\u7CFB\u7EDF\u4E3B\u8981\u529F\u80FD\u5165\u53E3\uFF0C\u5305\u62EC\u4F53\u68C0\u5957\u9910\u3001\u5065\u5EB7\u6863\u6848\u3001\u5065\u5EB7\u98CE\u9669\u8BC4\u4F30\u3001AI\u52A9\u624B\u7B49\u529F\u80FD\u5BFC\u822A\u3002'));
children.push(...imageWithCaption('mobile_index.png', '\u56FE13 \u79FB\u52A8\u7AEF\u9996\u9875', 240, 430));

children.push(bodyText('\uFF083\uFF09\u4F53\u68C0\u5957\u9910\uFF1A\u7528\u6237\u53EF\u4EE5\u6D4F\u89C8\u6240\u6709\u53EF\u7528\u7684\u4F53\u68C0\u5957\u9910\uFF0C\u67E5\u770B\u5957\u9910\u8BE6\u60C5\u548C\u4EF7\u683C\uFF0C\u8FDB\u884C\u5728\u7EBF\u9884\u7EA6\u3002'));
children.push(twoImagesRow(
    'mobile_setmeal.png', '\u56FE14 \u4F53\u68C0\u5957\u9910\u5217\u8868',
    'mobile_setmeal_detail.png', '\u56FE15 \u5957\u9910\u8BE6\u60C5\u9875\u9762'
));

children.push(bodyText('\uFF084\uFF09\u5065\u5EB7\u6863\u6848\uFF1A\u7528\u6237\u53EF\u4EE5\u67E5\u770B\u548C\u7BA1\u7406\u4E2A\u4EBA\u5065\u5EB7\u6863\u6848\uFF0C\u5305\u62EC\u5386\u6B21\u4F53\u68C0\u8BB0\u5F55\u548C\u5065\u5EB7\u6307\u6807\u53D8\u5316\u8D8B\u52BF\u3002'));
children.push(...imageWithCaption('mobile_healthrecord.png', '\u56FE16 \u5065\u5EB7\u6863\u6848\u9875\u9762', 240, 430));

children.push(bodyText('\uFF085\uFF09\u5065\u5EB7\u4FE1\u606F\u67E5\u8BE2\uFF1A\u63D0\u4F9B\u5065\u5EB7\u77E5\u8BC6\u6587\u7AE0\u6D4F\u89C8\u548C\u5065\u5EB7\u8D44\u8BAF\u63A8\u9001\u529F\u80FD\u3002'));
children.push(...imageWithCaption('mobile_healthinfo.png', '\u56FE17 \u5065\u5EB7\u4FE1\u606F\u9875\u9762', 240, 430));

children.push(bodyText('\uFF086\uFF09\u5065\u5EB7\u98CE\u9669\u8BC4\u4F30\uFF1A\u6839\u636E\u7528\u6237\u7684\u5065\u5EB7\u6570\u636E\u8FDB\u884C\u667A\u80FD\u98CE\u9669\u8BC4\u4F30\uFF0C\u7ED9\u51FA\u5065\u5EB7\u5EFA\u8BAE\u3002'));
children.push(...imageWithCaption('mobile_healthrisk.png', '\u56FE18 \u5065\u5EB7\u98CE\u9669\u8BC4\u4F30\u9875\u9762', 240, 430));

children.push(subTitle('6.3 AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B'));
children.push(bodyText('AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\u662F\u672C\u9879\u76EE\u7684\u6838\u5FC3\u521B\u65B0\u529F\u80FD\u4E4B\u4E00\uFF0C\u57FA\u4E8EDeepSeek\u5927\u6A21\u578B\u5B9E\u73B0\u3002\u7528\u6237\u53EF\u4EE5\u901A\u8FC7\u81EA\u7136\u8BED\u8A00\u63CF\u8FF0\u75C7\u72B6\u548C\u5065\u5EB7\u95EE\u9898\uFF0CAI\u52A9\u624B\u4F1A\u7ED9\u51FA\u4E13\u4E1A\u7684\u5065\u5EB7\u54A8\u8BE2\u5EFA\u8BAE\u548C\u4F53\u68C0\u65B9\u6848\u63A8\u8350\u3002\u7CFB\u7EDF\u91C7\u7528SSE\uFF08Server-Sent Events\uFF09\u6280\u672F\u5B9E\u73B0\u6D41\u5F0F\u54CD\u5E94\uFF0C\u524D\u7AEF\u9010\u5B57\u663E\u793AAI\u7684\u56DE\u7B54\u5185\u5BB9\uFF0C\u63D0\u4F9B\u4E86\u6D41\u7545\u7684\u4EA4\u4E92\u4F53\u9A8C\u3002'));
children.push(...imageWithCaption('ai-chat.png', '\u56FE19 AI\u667A\u80FD\u5065\u5EB7\u52A9\u624B\u5BF9\u8BDD\u754C\u9762', 480, 270));

children.push(bodyText('\u7EFC\u4E0A\u6240\u8FF0\uFF0C\u672C\u9879\u76EE' + LQ + '\u533B\u7597\u7BA1\u5BB6\u5065\u5EB7\u7BA1\u7406\u7CFB\u7EDF' + RQ + '\u5DF2\u6210\u529F\u5B9E\u73B0\u4E86\u5168\u90E8\u9884\u671F\u529F\u80FD\uFF0C\u6DB5\u76D6PC\u7F51\u9875\u7AEF\u548C\u79FB\u52A8\u7AEF\u5171\u8BA120\u4F59\u4E2A\u529F\u80FD\u9875\u9762\uFF0C\u5305\u542B19\u5F20\u7CFB\u7EDF\u622A\u56FE\u4E2D\u5C55\u793A\u7684\u6838\u5FC3\u529F\u80FD\u6A21\u5757\u3002\u7CFB\u7EDF\u529F\u80FD\u5B8C\u5584\u3001\u754C\u9762\u53CB\u597D\u3001\u6280\u672F\u67B6\u6784\u5408\u7406\uFF0C\u8FBE\u5230\u4E86\u5B9E\u8BAD\u9879\u76EE\u7684\u9884\u671F\u76EE\u6807\u3002'));

// ============================================================
// Create the document
// ============================================================
const doc = new Document({
    styles: {
        default: {
            document: {
                run: { font: '\u5B8B\u4F53', size: 21 },
            },
        },
    },
    sections: [{
        properties: {
            page: {
                size: { width: 11906, height: 16838 },
                margin: { top: 1440, right: 1200, bottom: 1440, left: 1200 },
            },
        },
        children: children,
    }],
});

const outputPath = path.join(__dirname, '\u8F6F\u4EF6231_\u4F55\u8FC8_Tlias\u9879\u76EE\u62A5\u544A\uFF08\u5B9E\u8BAD3\uFF09_\u5DF2\u5B8C\u6210.docx');

Packer.toBuffer(doc).then(buffer => {
    fs.writeFileSync(outputPath, buffer);
    console.log('Report generated: ' + outputPath);
    console.log('File size: ' + (buffer.length / 1024).toFixed(1) + ' KB');
}).catch(err => {
    console.error('Error:', err);
});
