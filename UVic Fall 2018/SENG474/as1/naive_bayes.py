import pandas as pd
import math

class multinomial_naive_bayes(object):

    def __init__(self, alpha=1.0):
        self.class_probability = {0:0, 1:0}
        self.word_prob_class_zero = {}
        self.word_prob_class_one = {}
        
        self.number_docs_class_zero = 0
        self.number_docs_class_one = 0
        self.class_zero_vocab = 0
        self.class_one_vocab = 0
        self.total_num_docs = 0
        self.alpha = alpha
        
    def get_class_distribution(self):
        train_lines = open('trainlabels.txt').readlines()
        self.total_num_docs = len(train_lines)
        for line in train_lines:
            class_val = int(line.split()[0])
            self.class_probability[class_val] += 1
        
        self.number_docs_class_zero = self.class_probability[0]
        self.number_docs_class_one = self.class_probability[1]
        self.class_probability[0] /= self.total_num_docs
        self.class_probability[1] /= self.total_num_docs
        
        print("Probability of each class:")
        print("\n".join("{}: {}".format(k, v) for k, v in self.class_probability.items()))
    
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
                for word in words_in_doc:
                    if self.word_prob_class_zero.get(word) != None:
                        self.word_prob_class_zero[word] += 1
                    else:
                        self.word_prob_class_zero[word] = 1
            # Update/set word count for class 1
            elif doc_label == 1:
                for word in words_in_doc:
                    if self.word_prob_class_one.get(word) != None:
                        self.word_prob_class_one[word] += 1
                    else:
                        self.word_prob_class_one[word] = 1

            start_index += 1

        self.class_zero_vocab = len(self.word_prob_class_zero.keys())
        self.class_one_vocab = len(self.word_prob_class_one.keys())

    def get_word_probabilities(self):
        for word, count in self.word_prob_class_zero.items():
            self.word_prob_class_zero[word] = (count + self.alpha) / (self.number_docs_class_zero + self.class_zero_vocab)

        for word, count in self.word_prob_class_one.items():
            self.word_prob_class_one[word] = (count + self.alpha) / (self.number_docs_class_one + self.class_one_vocab)

    def train(self):
        self.get_class_distribution()
        self.get_word_counts()
        self.get_word_probabilities()

    def test(self):
        # Smoothed probabilities are calculated below, these are used when a 
        # word in the test document is not found in the given class but is found
        # in another class's feature dict
        smooth_class_zero = math.log(1/(self.number_docs_class_zero + self.total_num_docs))
        smooth_class_one = math.log(1/(self.number_docs_class_one + self.total_num_docs))

        test_data_lines = open('testdata.txt').readlines()
        test_label_lines = open('testlabels.txt').readlines()
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
                if (self.word_prob_class_zero.get(word) != None):
                    score_class_zero += math.log(self.word_prob_class_zero[word])
                else:
                    score_class_zero += smooth_class_zero

                # Update class one score
                if (self.word_prob_class_one.get(word) != None):
                    score_class_one += math.log(self.word_prob_class_one[word])
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
