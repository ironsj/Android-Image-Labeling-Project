# Android Food Labeling App Tutorial

## Overview
My platform focus topic for my semester project in CIS 357 is TensorFlow for Android. TensorFlow is an open-source library for machine learning and artificial intelligence. TensorFlow Lite, or TF Lite, provides APIs for Mobile Apps to use TensorFlow models. The models are optimized to be more efficient on devices like mobile phones.

TensorFlow models are mainly used for use cases such as voice/speech rocognition, text recognition, image recognition, and more. Developers have the ability to create their own machine learning and AI models without getting into all of the nitty-gritty details. Developers can spend more time focusing on the actual logic of their model and TensorFlow will take care of the details behind the scenes. TensorFlow offers many pre-trained models that other developers can implement into their project. This is the case with my mobile application.

TensorFlow Lite converts pre-trained models into a special format that optimizes speed and storage. That is why it is especially useful on smaller capacity devices such as mobile phones or embedded devices like Raspberry Pi. 

For my project I have created a mobile app that allows Android users to take a picture or select a photo from their gallery of a food dish and the app will identify what the dish is. It will give three different options ranked by the probability of what the dish is. The user will also be able to see what these probabilities are. The TF Lite model I used was a food classification model published by Google. A link to the model can be found [here](https://tfhub.dev/google/lite-model/aiy/vision/classifier/food_V1/1). Other ready-to-deploy models can be found at [this link](https://tfhub.dev/).

The model I have chosen to implement into my app has the ability to recognize 2023 different food dishes. It is not able to decide whether or not something is edible or specify ingredients within a dish. If the input image is not of a food dish, or the image is not properly cropped, the output from the model may have no meaning. Google has also noted that the model was trained with a dataset skewed towards North American foods.

In the image below you can see an example of the application classifying the default image, a cheese burger. 

![Example of the app running. The image shows the application correctly identifying a cheeseburger.]()



```markdown
Syntax highlighted code block

# Header 1
## Header 2
### Header 3

- Bulleted
- List

1. Numbered
2. List

**Bold** and _Italic_ and `Code` text

[Link](url) and ![Image](src)
```

For more details see [Basic writing and formatting syntax](https://docs.github.com/en/github/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax).

### Jekyll Themes

Your Pages site will use the layout and styles from the Jekyll theme you have 
selected in your [repository settings](https://github.com/ironsj/Android-Image-Labeling-Project/settings/pages). 
The name of this theme is saved in the Jekyll `_config.yml` configuration file.

### Support or Contact

Having trouble with Pages? Check out our [documentation](https://docs.github.com/categories/github-pages-basics/) or [contact support](https://support.github.com/contact) and we’ll help you sort it out.
