package support.helper;

public enum SearchType {
    SIMPLE(SimpleSearchHelper.class),
    PHRASE(PhraseSearchHelper.class),
    WILDCARD(WildcardSearchHelper.class),
    FUZZY(FuzzySearchHelper.class);


    private Class<? extends SearchHelper> queryClz;

    <T extends SearchHelper>SearchType(Class<T> queryClz) {
        this.queryClz = queryClz;
    }

    public SearchHelper getSearchHelper() {
        try {
            return this.queryClz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Initialization failed. : Could not create a SearchHelper instance.");
        }
    }
}
