<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:wrapper>
    <jsp:attribute name="page_before">
        <link rel="stylesheet" type="text/css" href="<c:url value="/css/slick.min.css" />" />
<link rel="stylesheet" type="text/css" href="<c:url value="/css/slick-theme.min.css" />" />
    </jsp:attribute>

    <jsp:attribute name="page_heading">

    My Ad Web
  </jsp:attribute>

    <jsp:attribute name="page_body">
<div id="container"></div>
<script src="<c:url value="/js/react.js" />"></script>
<script src="<c:url value="/js/react-dom.js" />"></script>
<script src="<c:url value="/js/browser.min.js"/>"></script>
<script src="<c:url value="/js/react-slick.js"/>"></script>

<script type="text/babel">

  //import React from 'react';

  const adId = '${i}';

  class AdSlider extends React.Component {
      constructor(props) {
          super(props);
          this.state = {
              isLoading: true
          };
      }

      render () {
      var settings = {
          autoplay: true,
          dots: true,
          infinite: true,
          speed: 500,
          slidesToShow: 1,
          slidesToScroll: 1
      };
          var imgXHTML = [];

          for (let i = 0; i < this.props.imgs.length; i++) {
              imgXHTML.push(<div><img className="img-responsive" src={'<c:url value="/img/"/>' + this.props.imgs[i]} /></div>);
          }

      return (
              <Slider {...settings}>
          {imgXHTML}
              </Slider>
        );
      }
  }


  class AdView extends React.Component {
    constructor(props) {
      super(props);
        this.state ={
            ad: {
                imgs: ['None'],
                user : {
                    name: '',
                    email: '',
                    contactNo: ''
                }
            },
            title: '',
        }
        this.getAd = this.getAd.bind(this);
    }

      componentDidMount() {
          this.getAd();
      }

      getAd(){
      fetch('<c:url value="/get/"/>' + adId)
              .then((res) => {
                  return res.json();
              })
              .then((res) => {
                  this.setState({ ad: res });
              });
  }
    render() {
      return(
              <div>
              <div className="row">
                      <div className="col-md-12">
                      <h4>{this.state.ad.title}</h4>
                         <AdSlider imgs={this.state.ad.imgs} />
                        <div className="row">
                            <div className="col-md-6 ad-desc-2">
                               <b>Description</b>
                               <p>{this.state.ad.body}</p>
                            </div>
                            <div className="col-md-6 ad-desc-2">
                                <dl className="dl-horizontal">
                                      <dt>Posted Date</dt>
                                      <dd>{this.state.ad.postedDate}</dd>
                                      <dt>Location</dt>
                                      <dd>{this.state.ad.location}</dd>
                                      <dt>Seller</dt>
                                      <dd>{this.state.ad.user.name}</dd>
                                      <dt>Seller Contact No</dt>
                                      <dd><a href={'tel:' + this.state.ad.user.contactNo}>{this.state.ad.user.contactNo}</a></dd>
                                      <dt>Seller Email</dt>
                                      <dd><a href={'mailto:' + this.state.ad.user.email}>{this.state.ad.user.email}</a></dd>
                                      <dt>Price</dt>
                                      <dd><b>{this.state.ad.price}</b></dd>
                             </dl>
                            </div>
                        </div>
                      </div>
              </div>
              </div>);
    }
  }

  //export default AdView;
  ReactDOM.render(
  <AdView />,
  document.getElementById('container')
  );
</script>
    </jsp:attribute>
</t:wrapper>


