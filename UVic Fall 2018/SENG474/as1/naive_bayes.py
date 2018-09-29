import pandas as pd
import math

'''
    SENG 474 Assignment 1
    Cole Boothman
    Multinomial Naive Bayes Text Classifier 
'''

class multinomial_naive_bayes(object):

    def __init__(self, alpha=1.0):
        self.class_probability = {0:0, 1:0}
        self.word_count_class_zero = {}
        self.word_count_class_one = {}
        self.vocab = {}
        self.word_probabilities = {0: {}, 1: {}}       
        self.class_zero_vocab = 0
        self.class_one_vocab = 0
        self.alpha = alpha
        
    def get_class_distribution(self):
        train_lines = open('trainlabels.txt').readlines()
        for line in train_lines:
            class_val = int(line.split()[0])
            self.class_probability[class_val] += 1
        
        self.class_probability[0] /= len(train_lines)
        self.class_probability[1] /= len(train_lines)
        
        print("Probability of each class:")
        print("\n".join("{}: {}".format(k, v) for k, v in self.class_probability.items()))

    def get_entire_vocabulary(self):
        train_data_lines = open('traindata.txt').readlines()

        for line in train_data_lines:
             # Remove newline chars and separate doc into words
            line = line.rstrip()
            words_in_doc = line.split(' ')

            for word in words_in_doc:
                self.vocab[word] = 1

    def get_word_counts(self):
        train_data_lines = open('traindata.txt').readlines()
        train_label_lines = open('trainlabels.txt').readlines()
        start_index = 0

        for line in train_data_lines:
            # Remove newline chars and separate doc into words
            line = line.rstrip()
            words_in_doc = line.split(' ')
   
            doc_label = int(train_label_lines[start_index].split()[0])
            # Update/set word count for class 0
            if doc_label == 0:
                self.class_zero_vocab += len(words_in_doc)

                for word in words_in_doc:
                    if self.word_count_class_zero.get(word) != None:
                        self.word_count_class_zero[word] += 1
                    else:
                        self.word_count_class_zero[word] = 1
            # Update/set word count for class 1
            elif doc_label == 1:
                self.class_one_vocab += len(words_in_doc)

                for word in words_in_doc:
                    if self.word_count_class_one.get(word) != None:
                        self.word_count_class_one[word] += 1
                    else:
                        self.word_count_class_one[word] = 1

            start_index += 1

    def get_word_probabilities(self):
        for word in self.vocab.keys():
            if self.word_count_class_zero.get(word) == None:
                class_zero_count = 0
            else:
                class_zero_count = self.word_count_class_zero[word]
            
            if self.word_count_class_one.get(word) == None:
                class_one_count = 0
            else:
                class_one_count = self.word_count_class_one[word]
        
            self.word_probabilities[0][word] = class_zero_count + self.alpha / (self.class_zero_vocab + len(self.vocab.keys()))
            self.word_probabilities[1][word] = class_one_count + self.alpha / (self.class_one_vocab + len(self.vocab.keys()))
            

    def train(self):
        self.get_class_distribution()
        self.get_entire_vocabulary()
        self.get_word_counts()
        self.get_word_probabilities()

    def test(self):
        # Smoothed probabilities are calculated below, these are used when a 
        # word in the test document is not found in the given class but is found
        # in another class's feature dict
        smooth_class_zero = math.log(1/(self.class_zero_vocab + len(self.vocab.keys())))
        smooth_class_one = math.log(1/(self.class_one_vocab + len(self.vocab.keys())))

        test_data_lines = open('traindata.txt').readlines()
        test_label_lines = open('trainlabels.txt').readlines()
        num_test_docs = len(test_data_lines)
        documents_classified_correct = 0
        documents_classified_incorrect = 0
        start_index = 0

        for line in test_data_lines:
            line = line.rstrip()
            words_in_doc = line.split(' ')

            doc_label = int(test_label_lines[start_index].split()[0])
            score_class_zero = math.log(self.class_probability[0])
            score_class_one = math.log(self.class_probability[1])

            for word in words_in_doc:
                # Update class zero score
                if (self.word_probabilities[0].get(word) != None):
                    score_class_zero += math.log(self.word_probabilities[0][word])
                else:
                    score_class_zero += smooth_class_zero

                # Update class one score
                if (self.word_probabilities[1].get(word) != None):
                    score_class_one += math.log(self.word_probabilities[1][word])
                else:
                    score_class_one += smooth_class_one


            if (score_class_one > score_class_zero):
                # print('Predicted Class One, Actual Label: {}'.format(doc_label))
                if (doc_label == 1):
                    documents_classified_correct += 1
                else:
                    documents_classified_incorrect +=1

            elif (score_class_one < score_class_zero):
                # print('Predicted Class Zero, Actual Label: {}'.format(doc_label))
                if (doc_label == 0):
                    documents_classified_correct += 1
                else:
                    documents_classified_incorrect +=1

            start_index += 1

        documents_classified_correct /= num_test_docs
        documents_classified_incorrect /= num_test_docs
        print('Results: Classified {} of Documents Correctly, Classified {} of Documents incorrectly'
            .format(documents_classified_correct, documents_classified_incorrect))

model = multinomial_naive_bayes()
model.train()
model.test()
