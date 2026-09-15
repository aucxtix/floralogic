import sys
with open('app/src/main/java/com/example/ui/KisanViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("CropDiseaseDetector.analyzeCropLeaf(bitmap, cropHint, specimenId)", "CropDiseaseDetector.analyzeCropLeaf(getApplication(), bitmap, cropHint, specimenId)")

with open('app/src/main/java/com/example/ui/KisanViewModel.kt', 'w') as f:
    f.write(content)
print("Done")
