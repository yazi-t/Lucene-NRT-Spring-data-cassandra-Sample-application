<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:wrapper>
    <jsp:attribute name="page_heading">
    My Ad Web
  </jsp:attribute>

    <jsp:attribute name="page_body">
<div id="container"></div>

<script src="<c:url value="js/react.js" />"></script>
<script src="<c:url value="js/react-dom.js" />"></script>
<script src="<c:url value="js/browser.min.js"/>"></script>
<script type="text/babel">

    var _adLimit = 10;

    const ALL = 'all';

    var AdDetail = React.createClass({
        render() {
            return <div>
                <div className="row">
                    <div className="col-md-12">
                        <h2><a className="ad-title" href={'<c:url value="/ad/"/>' + this.props.id}>
                            <span className="ad-title-num">{this.props.i}</span> {this.props.title}
                            <i className="glyphicon glyphicon-arrow-right ad-title-arrow"></i></a></h2>
                        <div className="col-md-8">
                            <p>{this.props.description}</p>
                        </div>
                        <div className="col-md-4">
                            <img src={(this.props.imgUrl !== null && this.props.imgUrl !== undefined) ? '<c:url value="/"/>' + this.props.imgUrl: ''} className="img-responsive" width="200px"/>
                        </div>
                    </div>
                </div>
            </div>;
        }
    });

    var Application = React.createClass({
        getInitialState: function () {
            return {
                category: ALL,
                location: ALL,
                searchText: '',
                adCategories: [],
                locations: []
            };
        },
        componentDidMount: function () {
            this.getAllAdCategories();
            this.getAllLocations();
        },
        handleLocationChange: function (e) {
            let location = e.target.value;
            let index = e.nativeEvent.target.selectedIndex;
            this.setState({location: e.nativeEvent.target[index].text},
                function () {
                this.props.setLocation(location);
                });
        },
        handleAdTypeChange: function (e) {
            let category = e.target.value;
            let index = e.nativeEvent.target.selectedIndex;
            this.setState({category: e.nativeEvent.target[index].text},
                function () {
                this.props.setCategory(category);
                });
        },
        handleSearchOnclick: function () {
            this.props.setIsSearch(true);
            this.props.searchAds(true);
        },
        getAllAdCategories: function () {
            fetch('<c:url value="/ad-categories"/>')
                .then((res) => {
                    return res.json();
                })
                .then((res) => {
                    this.setState({adCategories: res});
                });
        },
        getAllLocations: function () {
            fetch('<c:url value="/locations"/>')
                .then((res) => {
                    return res.json();
                })
                .then((res) => {
                    this.setState({locations: res});
                });
        }, updateSearchText: function (evt) {
            let text = evt.target.value;
            this.setState({searchText: text}, function () {
                this.props.setSearchText(text);
            });
        },

        render() {
            var msg1 = 'Search filters category: ' + this.state.category + ', location: ' + this.state.location + ', Title: ' + this.state.searchText;
            return <div>
                <div className="row ad-search-bar">
                    <div className="col-md-3">
                        <select className="form-control" onChange={this.handleAdTypeChange}>
                            <option value={ALL} defaultValue>All</option>
                            {
                                this.state.adCategories.map(function (element) {
                                    return <option value={element.id}>{element.name}</option>
                                })
                            }
                        </select>
                    </div>
                    <div className="col-md-3">
                        <select className="form-control" onChange={this.handleLocationChange}>
                            <option value={ALL} defaultValue>All</option>
                            {
                                this.state.locations.map(function (element) {
                                    return <option value={element.id}>{element.name}</option>
                                })
                            }
                        </select>
                    </div>
                    <div className="col-md-3">
                        <input type="text" className="form-control" onChange={this.updateSearchText}/>
                    </div>
                    <div className="col-md-3">
                        <button className="btn" onClick={this.handleSearchOnclick}>
                            Search
                        </button>
                    </div>
                </div>
                <div className="ad-search-msg"><p>{msg1}</p></div>
            </div>;
        }
    });

    var AdSectionLayout = React.createClass({
        getInitialState: function () {
            return {
                ads: [],
                dataRetType: 0
            };
        },
        componentDidMount: function () {
            this.getAllAds(true);
        },
        getAllAds: function (resetPage) {
            let url = '<c:url value="/all/"/>';
            fetch(url, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({
                    resetPage: resetPage
                })
            })
                .then((res) => {
                    return res.json();
                })
                .then((res) => {
                if (resetPage) {
                    this.setState({ads: res.content});
                } else {
                    let newAds = res.content;

                    for (let i = 0; i < newAds.length; i++) {
                        this.state.ads.push(newAds[i]);
                    }
                }
                this.props.setShowNextButton(res.hasNext);
                });
        },
        searchAds: function (resetPage) {
            let url = '<c:url value="/search/"/>';
            fetch(url, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    phrase: this.props.searchText,
                    location: this.props.location,
                    category: this.props.category,
                    resetPage: resetPage
                })
            })
                .then((res) => {
                    return res.json();
                })
                .then((res) => {
                    if (resetPage) {
                        this.setState({ads: res.content});
                    } else {
                        let newAds = res.content;

                        for (let i = 0; i < newAds.length; i++) {
                            this.state.ads.push(newAds[i]);
                        }
                    }
                    this.props.setShowNextButton(res.hasNext);
                });
        },
        render() {
            let indents = [];
            for (let i = 0; i < this.state.ads.length; i++) {
                indents.push(<AdDetail i={i} id={this.state.ads[i].id} title={this.state.ads[i].title}
                                       description={this.state.ads[i].body} imgUrl={(this.state.ads[i].imgs !== null && this.state.ads[i].imgs !== undefined) ? this.state.ads[i].imgs[0] : "no-img.jpg"}/>);
            }
            return <div>{indents}</div>;
        }
    });

    var MainLayout = React.createClass({
        getInitialState: function () {
            return {
                location: ALL,
                category: ALL,
                searchText: '',
                showPrevBtn: false,
                showNextBtn: false,
                isSearch: false
            };
        },
        searchAds : function () {
            this.refs.adSec.searchAds(true);
        },
        refreshAds: function (location) {
            this.setState({location: location});
        },
        setSearchText: function (text) {
            this.setState({searchText: text});
        },
        setLocation: function (location) {
            this.setState({location: location});
        },
        setCategory: function (category) {
            this.setState({category: category});
        },
        setShowNextButton: function (status) {
            this.setState({showNextBtn: status});
        },
        setIsSearch: function (isSearch) {
            this.setState({isSearch: isSearch});
        },
        handleOnNext: function () {
            if (this.state.isSearch) {
                this.refs.adSec.searchAds(false);
            } else {
                this.refs.adSec.getAllAds(false);
            }
        },
        render() {
            return <div>
                <Application     searchAds={this.searchAds} refreshAds={this.refreshAds} setSearchText={this.setSearchText}
                                 setLocation={this.setLocation} setCategory={this.setCategory} setIsSearch={this.setIsSearch}/>
                <AdSectionLayout location={this.state.location} category={this.state.category}
                                 searchText={this.state.searchText} setShowNextButton={this.setShowNextButton}
                                 setIsSearch={this.setIsSearch} ref="adSec"/>

                <div className="btn-group">
                    {this.state.showNextBtn ? <button onClick={this.handleOnNext} className="btn btn-grp-custom">Load more</button> : null}
                </div>
            </div>;
        }
    });
    ReactDOM.render(<MainLayout/>, document.getElementById('container'));
</script>
    </jsp:attribute>
</t:wrapper>