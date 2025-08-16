import re
from typing import List, Tuple
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
from difflib import SequenceMatcher
from sentence_transformers import SentenceTransformer  # Локальная модель

class FreeBibliographyComparator:
    def __init__(self):
        """Инициализация с бесплатными инструментами"""
        self.vectorizer = TfidfVectorizer()
        self.sbert_model = SentenceTransformer('all-MiniLM-L6-v2')  # Лёгкая локальная модель

    def clean_text(self, text: str) -> str:
        """Очистка текста от мусора"""
        text = re.sub(r'[^\w\s.,;:()\[\]{}-]', '', text)  # Удаляем спецсимволы, кроме пунктуации
        text = re.sub(r'\s+', ' ', text).strip().lower()
        return text

    def structural_similarity(self, a: str, b: str) -> float:
        """Сравнение на уровне структуры строки"""
        return SequenceMatcher(None, a, b).ratio()

    def tfidf_similarity(self, texts: List[str]) -> float:
        """Косинусная схожесть по TF-IDF"""
        cleaned = [self.clean_text(t) for t in texts]
        matrix = self.vectorizer.fit_transform(cleaned)
        return cosine_similarity(matrix)[0, 1]

    def semantic_similarity(self, a: str, b: str) -> float:
        """Семантическая схожесть через Sentence-BERT"""
        embeddings = self.sbert_model.encode([a, b])
        return cosine_similarity(embeddings[0:1], embeddings[1:2])[0][0]

    def hybrid_score(self, ref1: str, ref2: str) -> float:
        """Комбинированная оценка (можно настроить веса)"""
        structural = self.structural_similarity(ref1, ref2)
        tfidf = self.tfidf_similarity([ref1, ref2])
        semantic = self.semantic_similarity(ref1, ref2)
        
        # Веса: структурная (0.3), TF-IDF (0.4), семантическая (0.3)
        return 0.3*structural + 0.4*tfidf + 0.3*semantic

    def find_duplicates(self, references: List[str], threshold: float = 0.75) -> List[Tuple[int, int, float]]:
        """Поиск похожих описаний в списке"""
        pairs = []
        for i in range(len(references)):
            for j in range(i+1, len(references)):
                score = self.hybrid_score(references[i], references[j])
                if score >= threshold:
                    pairs.append((i, j, round(score, 2)))
        return sorted(pairs, key=lambda x: x[2], reverse=True)

# Пример использования
if __name__ == "__main__":
    # Инициализация (скачает модель при первом запуске ~80 МБ)
    comparator = FreeBibliographyComparator()

    # Тестовые данные (разные форматы одной работы + посторонняя)
    test_refs = [
        "Smith, J. (2020). The Art of Programming. Journal of CS, 15(2), 123-145.",
        "Smith, John. \"The Art of Programming.\" Journal of Computer Science, vol. 15, no. 2, 2020, pp. 123-145.",
        "J. Smith. The Art of Programming // J. Comput. Sci. 2020. V.15. P.123-145.",
        "Brown, A. (2021). Data Science Methods. MIT Press."
    ]

    # Анализ
    duplicates = comparator.find_duplicates(test_refs)
    
    print("Найдены возможные дубликаты:")
    for i, j, score in duplicates:
        print(f"\nСхожесть: {score:.0%}")
        print(f"A: {test_refs[i]}")
        print(f"B: {test_refs[j]}")
