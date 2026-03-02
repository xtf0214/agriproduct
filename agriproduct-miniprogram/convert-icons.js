const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

const tabbarDir = path.join(__dirname, 'src/static/tabbar');

// 获取所有 SVG 文件
const svgFiles = fs.readdirSync(tabbarDir).filter(file => file.endsWith('.svg'));

console.log(`Found ${svgFiles.length} SVG files to convert...`);

// 转换每个 SVG 文件为 PNG
svgFiles.forEach(async (file) => {
  const inputPath = path.join(tabbarDir, file);
  const outputPath = path.join(tabbarDir, file.replace('.svg', '.png'));
  
  try {
    await sharp(inputPath)
      .resize(81, 81) // 小程序 tabbar 图标推荐尺寸
      .png()
      .toFile(outputPath);
    
    console.log(`✓ Converted: ${file} -> ${file.replace('.svg', '.png')}`);
  } catch (error) {
    console.error(`✗ Failed to convert ${file}:`, error.message);
  }
});

console.log('\nConversion completed!');
