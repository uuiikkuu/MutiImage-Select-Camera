# MutiImage-Select-Camera
多图选择和拍照

介绍：集成多图选择和拍照功能，并且内置图片压缩

1、只要将其作为库添加进去即可

2、使用： 

//createMultiImagesPickerBuilder代码示例
                ImageModule.MultiPagerPicker lMultiPagerPicker = new ImageModule.MultiPagerPicker() {
                    @Override
                    public void onImagePickSucc(ArrayList<String> resultList) {
                       //成功的处理
                    }
                };

ImageModule.getInstance()
                        .createMultiImagesPickerBuilder(mContext, lMultiPagerPicker)
                        .setSelectCount(1)//设置选择图片的数量
                .setSingleChooseAndCutMode()//设置单选模式
//                .setShowCamera(true)//是否显示相机按钮
                        .startPick();
